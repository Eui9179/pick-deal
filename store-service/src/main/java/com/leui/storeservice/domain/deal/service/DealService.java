package com.leui.storeservice.domain.deal.service;

import com.leui.storeservice.domain.deal.entity.Deal;
import com.leui.storeservice.domain.deal.entity.DealReservation;
import com.leui.storeservice.domain.deal.repository.DealRepository;
import com.leui.storeservice.domain.deal.repository.DealReservationRepository;
import com.leui.storeservice.domain.discountpolicy.calculator.DiscountCalculator;
import dto.store.*;
import exception.OutOfStockException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.RedisRepository;

import java.time.Instant;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class DealService {

    private final DealRepository dealRepository;
    private final DiscountCalculator calculator;
    private final DealReservationRepository dealReservationRepository;
    private final RedisRepository redisRepository;

    // Redis 관련 상수 (현재 미사용)
    private static final String DEAL_RESERVATION_ZSET = "deal:reservations:";
    private static final String DEAL_TOTAL_QTY = "deal:totalQty:";
    private static final String ORDER_QTY_PREFIX = "order:qty:";

    private final DefaultRedisScript<Long> reserveStockScript;

    public DealService(DealRepository dealRepository,
                       DiscountCalculator calculator,
                       DealReservationRepository dealReservationRepository,
                       RedisRepository redisRepository) {
        this.dealRepository = dealRepository;
        this.calculator = calculator;
        this.dealReservationRepository = dealReservationRepository;
        this.redisRepository = redisRepository;
        this.reserveStockScript = loadReserveStockScript();
    }

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
    public DealQuantityResponse confirmStock(Long dealId, DealStockQuantityRequest request) {
        int stockQuantity = dealRepository.decreaseStockQuantity(dealId, request.quantity());
        if (stockQuantity == 0) {
            if (!dealRepository.existsById(dealId)) {
                throw new EntityNotFoundException("Deal Not Found. dealId: " + dealId);
            }
            throw new OutOfStockException("Out of Stock. dealId: " + dealId);
        }

        dealReservationRepository.deleteByOrderId(request.orderId());

        return new DealQuantityResponse(stockQuantity);
    }

    @Transactional
    public DealQuantityResponse reserveStock(Long dealId, DealStockQuantityRequest request, Long userId) {
        Deal deal = dealRepository.findByIdWithLock(dealId)
                .orElseThrow(() -> new EntityNotFoundException("Deal not found. id = " + dealId));

        Long reservedQuantity = dealReservationRepository.sumQuantityByDealId(dealId);

        long availableStock = deal.getStockQuantity() - (reservedQuantity != null ? reservedQuantity : 0);
        if (availableStock < request.quantity()) {
            throw new OutOfStockException("Out of Stock. dealId: " + dealId);
        }

        long expiredAt = Instant.now().plusSeconds(900).toEpochMilli();
        DealReservation reservation = new DealReservation(
                dealId,
                request.orderId(),
                userId,
                request.quantity(),
                expiredAt
        );
        dealReservationRepository.save(reservation);
        return new DealQuantityResponse(availableStock - request.quantity());
    }

    @Transactional
    public void rollbackStock(DealStockQuantityRequest request) {
        dealReservationRepository.deleteByOrderId(request.orderId());
    }

    /**
     * 재고 감소 레디스 버전
     */
    @SuppressWarnings("unused")
    private Long saveAtomicInRedis(Long dealId, long stockQuantity, String orderId, long requestQuantity, long expiredAt) {
        long currentTime = Instant.now().toEpochMilli();

        List<String> keys = List.of(
                DEAL_RESERVATION_ZSET + dealId,   // KEYS[1]: deal:reservations:{dealId}
                DEAL_TOTAL_QTY + dealId,          // KEYS[2]: deal:totalQty:{dealId}
                ORDER_QTY_PREFIX                   // KEYS[3]: order:qty:
        );

        Object[] args = new Object[]{
                String.valueOf(stockQuantity),     // ARGV[1]: quantity
                orderId,                           // ARGV[2]: orderId
                String.valueOf(requestQuantity),   // ARGV[3]: requestQty
                String.valueOf(expiredAt),         // ARGV[4]: expiredAt
                String.valueOf(currentTime)        // ARGV[5]: currentTime
        };

        return redisRepository.executeScript(reserveStockScript, keys, args);
    }

    /**
     * Redis 예약 정보 삭제 (미사용)
     */
    @SuppressWarnings("unused")
    private void confirmInRedis(Long dealId, String orderId) {
        String orderQtyKey = ORDER_QTY_PREFIX + orderId;
        String totalQtyKey = DEAL_TOTAL_QTY + dealId;
        String reservationZsetKey = DEAL_RESERVATION_ZSET + dealId;

        String quantity = redisRepository.get(orderQtyKey);
        if (quantity != null) {
            redisRepository.remove(orderQtyKey);
            redisRepository.zSetRemove(reservationZsetKey, orderId);

            long qty = Long.parseLong(quantity);
            redisRepository.decrement(totalQtyKey, qty);
        }
    }

    /**
     * Lua 스크립트 로드
     */
    private DefaultRedisScript<Long> loadReserveStockScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("lua/reserve-deal-stock.lua"));
        script.setResultType(Long.class);
        return script;
    }
}
