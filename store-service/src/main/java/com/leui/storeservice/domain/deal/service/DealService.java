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

        long current = Instant.now().toEpochMilli();
        long expiredAt = Instant.now().plusSeconds(900).toEpochMilli();

        String luaScript = """
                
                """;
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(luaScript);

        List<String> keys = List.of(
                DEAL_RESERVATION + deal.getId(),
                RESERVATION_ORDER_ID
        );

        List<String> agrs = List.of(
                String.valueOf(deal.getStockQuantity()),
                String.valueOf(request.quantity())
        );



        long reservationCount = redisRepository.zSetCountRange(
                DEAL_RESERVATION + dealId,
                current,
                expiredAt
        ); // 현재 시간 ~ expiredAt 까지 조회

        // 현재 재고 - redis 안에 있는 재고 > 0 일때만 예약 가능
        if (deal.getStockQuantity() - reservationCount <= 0) {
            throw new OutOfStockException("Out of Stock. id: " + dealId);
        }

        // 재고 예약
        redisRepository.put(RESERVATION_ORDER_ID + request.orderId(), String.valueOf(request.quantity()));
        redisRepository.zSetAdd(
                DEAL_RESERVATION + dealId,
                RESERVATION_ORDER_ID + request.orderId(),
                expiredAt
        );

        // TODO 비동기 처리
        DealReservation dealReservation = new DealReservation(dealId, request.orderId(), userId, expiredAt);
        dealReservationRepository.save(dealReservation);
    }
}
