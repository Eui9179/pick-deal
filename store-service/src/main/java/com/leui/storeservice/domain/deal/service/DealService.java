package com.leui.storeservice.domain.deal.service;

import com.leui.storeservice.domain.deal.entity.Deal;
import com.leui.storeservice.domain.deal.entity.DealReservation;
import com.leui.storeservice.domain.deal.repository.DealRepository;
import com.leui.storeservice.domain.deal.repository.DealReservationRepository;
import com.leui.storeservice.domain.discountpolicy.calculator.DiscountCalculator;
import dto.store.*;
import exception.OutOfStockException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.RedisRepository;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DealService {

    private final DealRepository dealRepository;
    private final DiscountCalculator calculator;
    private final RedisRepository redisRepository;
    private final DealReservationRepository dealReservationRepository;

    private final String DEAL_RESERVATION = "deal-reservation:";
    private final String RESERVATION_ORDER_ID = "reservation-order-id:";

    public Deal create(Deal deal) {
        return dealRepository.save(deal);
    }

    public List<DealDetailResponse> getDeals(Long storeId) {
        return dealRepository.findAllByStoreIdWithDiscountPolicy(storeId)
                .stream()
                .map(deal -> new DealDetailResponse(
                        deal.getId(),
                        deal.getStore().getId(),
                        deal.getName(),
                        deal.getDescription(),
                        deal.getPrice(),
                        calculator.calculate(deal),
                        deal.getDiscountPolicy().getDiscountValue(),
                        deal.getStockQuantity(),
                        deal.getDealStatus(),
                        deal.getPickupEndTime()
                ))
                .toList();
    }

    public DealDetailResponse getDealDetail(Long dealId) {
        Deal deal = dealRepository.findByIdWithDiscountPolicy(dealId);
        return DealDetailResponse.from(
                deal.getId(),
                deal.getStore().getId(),
                deal.getName(),
                deal.getDescription(),
                deal.getPrice(),
                calculator.calculate(deal),
                deal.getDiscountPolicy().getDiscountValue(),
                deal.getStockQuantity(),
                deal.getDealStatus(),
                deal.getPickupEndTime()
        );
    }

    @Transactional
    public DealUpdateResponse updateDealContent(Long dealId, DealUpdateRequest request) {
        Deal deal = getDeal(dealId);
        return new DealUpdateResponse(deal.updateContent(request));
    }

    public Deal getDeal(Long dealId) {
        return dealRepository.findById(dealId)
                .orElseThrow(() -> new EntityNotFoundException("Deal not found. id = " + dealId));
    }

    @Transactional
    public DealStockDecreaseResponse confirmStock(Long dealId, DealStockDecreaseRequest request) {
        int stockQuantity = dealRepository.decreaseStockQuantity(dealId, request.quantity());
        if (stockQuantity == 0) {
            if (!dealRepository.existsById(dealId)) {
                throw new EntityNotFoundException("Deal Not Found. dealId: " + dealId);
            }
            throw new OutOfStockException("Out of Stock. dealId: " + dealId);
        }

        Set<String> expiredOrders = redisRepository.zSetGetRange(DEAL_RESERVATION + dealId, 0, Instant.now().toEpochMilli());
        redisRepository.remove(expiredOrders);

        Set<String> orderIds = new HashSet<>();
        for (String key : expiredOrders) {
            orderIds.add(key.substring(DEAL_RESERVATION.length()));
        }
        dealReservationRepository.deleteByIds(orderIds);

        return new DealStockDecreaseResponse(stockQuantity);
    }

    public void reserveStock(Long dealId, DealStockDecreaseRequest request, Long userId) {
        Deal deal = getDeal(dealId);

        long expiredAt = Instant.now().plusSeconds(900).toEpochMilli();
        saveAtomicInRedis(dealId, deal.getStockQuantity(), request.orderId(), request.quantity(), expiredAt);

        // TODO 비동기 처리
        DealReservation dealReservation = new DealReservation(dealId, request.orderId(), userId, expiredAt);
        dealReservationRepository.save(dealReservation);
    }

    private void saveAtomicInRedis(Long dealId, long stockQuantity, String orderId, long requestQuantity, long expiredAt) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(luaScript());

        long current = Instant.now().toEpochMilli();

        Map<String, String> orderQuantityMap = redisRepository.hashGetAll(DEAL_RESERVATION + dealId);
        long reservationQuantity = redisRepository.zSetGetRange(DEAL_RESERVATION + dealId, current, expiredAt)
                .stream()
                .mapToLong(oId -> Long.parseLong(orderQuantityMap.get(oId)))
                .sum();

        // 현재 재고 - redis 안에 있는 재고 > 0 일때만 예약 가능
        if (stockQuantity - reservationQuantity <= 0) {
            throw new OutOfStockException("Out of Stock. id: " + dealId);
        }

        // 재고 예약
        redisRepository.hashPut(RESERVATION_ORDER_ID + orderId, orderId, String.valueOf(requestQuantity));
        redisRepository.zSetAdd(
                DEAL_RESERVATION + dealId,
                RESERVATION_ORDER_ID + orderId,
                expiredAt
        );
    }

    private String luaScript() {
        return """
                -- Deal 재고 예약 Lua 스크립트
                -- KEYS[1]: deal-reservation:{dealId} (ZSet 키)
                -- ARGV[1]: stockQuantity (현재 재고)
                -- ARGV[2]: orderId (주문 ID)
                -- ARGV[3]: requestQuantity (요청 수량)
                -- ARGV[4]: expiredAt (만료 시간 milliseconds)
                -- ARGV[5]: currentTime (현재 시간 milliseconds)
                
                local dealReservationKey = KEYS[1]
                local reservationOrderKeyPrefix = "reservation-order-id:"
                local stockQuantity = tonumber(ARGV[1])
                local orderId = ARGV[2]
                local requestQuantity = tonumber(ARGV[3])
                local expiredAt = tonumber(ARGV[4])
                local currentTime = tonumber(ARGV[5])
                
                -- 1. ZSet에서 현재 시간 이후의 유효한 예약 목록 가져오기
                local validReservations = redis.call('ZRANGEBYSCORE', dealReservationKey, currentTime, '+inf')
                
                -- 2. 각 예약의 수량을 Hash에서 가져와서 합산
                local reservationQuantity = 0
                for _, reservationKey in ipairs(validReservations) do
                    -- reservationKey는 "reservation-order-id:{orderId}" 형태
                    -- orderId 추출
                    local oid = string.sub(reservationKey, #reservationOrderKeyPrefix + 1)
                    -- Hash에서 수량 가져오기
                    local quantity = redis.call('HGET', reservationKey, oid)
                    if quantity then
                        reservationQuantity = reservationQuantity + tonumber(quantity)
                    end
                end
                
                -- 3. 재고 확인 (현재 재고 - 예약된 수량 - 요청 수량)
                if stockQuantity - reservationQuantity - requestQuantity < 0 then
                    return -1  -- Out of stock
                end
                
                -- 4. Hash에 주문 정보 저장
                local newReservationKey = reservationOrderKeyPrefix .. orderId
                redis.call('HSET', newReservationKey, orderId, requestQuantity)
                
                -- 5. ZSet에 예약 정보 저장 (member는 reservation key, score는 만료 시간)
                redis.call('ZADD', dealReservationKey, expiredAt, newReservationKey)
                
                -- 6. 성공 - 남은 재고 반환
                return stockQuantity - reservationQuantity - requestQuantity
                """;
    }
}
