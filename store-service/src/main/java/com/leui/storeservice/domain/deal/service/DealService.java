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
    private final RedisRepository redisRepository;
    private final DealReservationRepository dealReservationRepository;

    private static final String DEAL_RESERVATION_ZSET = "deal:reservations:";
    private static final String DEAL_TOTAL_QTY = "deal:totalQty:";
    private static final String ORDER_QTY_PREFIX = "order:qty:";

    private final DefaultRedisScript<Long> reserveStockScript;

    public DealService(DealRepository dealRepository,
                       DiscountCalculator calculator,
                       RedisRepository redisRepository,
                       DealReservationRepository dealReservationRepository
    ) {
        this.dealRepository = dealRepository;
        this.calculator = calculator;
        this.redisRepository = redisRepository;
        this.dealReservationRepository = dealReservationRepository;
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
    public DealStockDecreaseResponse confirmStock(Long dealId, DealStockDecreaseRequest request) {
        int stockQuantity = dealRepository.decreaseStockQuantity(dealId, request.quantity());
        if (stockQuantity == 0) {
            if (!dealRepository.existsById(dealId)) {
                throw new EntityNotFoundException("Deal Not Found. dealId: " + dealId);
            }
            throw new OutOfStockException("Out of Stock. dealId: " + dealId);
        }

        confirmInRedis(dealId, request.orderId());

        return new DealStockDecreaseResponse(stockQuantity);
    }

    public Long reserveStock(Long dealId, DealStockDecreaseRequest request, Long userId) {
        Deal deal = getDeal(dealId);
        long expiredAt = Instant.now().plusSeconds(900).toEpochMilli();

        Long result = saveAtomicInRedis(dealId, deal.getStockQuantity(), request.orderId(), request.quantity(), expiredAt);

        if (result == null || result == -1) {
            throw new OutOfStockException("Out of Stock. dealId: " + dealId);
        }

        return result;
    }

    private void confirmInRedis(Long dealId, String orderId) {
        // 결제 완료된 주문의 Redis 예약 데이터 삭제
        String orderQtyKey = ORDER_QTY_PREFIX + orderId;
        String totalQtyKey = DEAL_TOTAL_QTY + dealId;
        String reservationZsetKey = DEAL_RESERVATION_ZSET + dealId;

        // 주문별 수량 정보 조회 후 삭제
        String quantity = redisRepository.get(orderQtyKey);
        if (quantity != null) {
            redisRepository.remove(orderQtyKey);
            redisRepository.zSetRemove(reservationZsetKey, orderId);

            // 총 예약 수량 감소
            long qty = Long.parseLong(quantity);
            redisRepository.decrement(totalQtyKey, qty);
        }
    }

    private Long saveAtomicInRedis(Long dealId, long stockQuantity, String orderId, long requestQuantity, long expiredAt) {
        long currentTime = Instant.now().toEpochMilli();

        List<String> keys = List.of(
                DEAL_RESERVATION_ZSET + dealId,   // KEYS[1]: deal:reservations:{dealId}
                DEAL_TOTAL_QTY + dealId,          // KEYS[2]: deal:totalQty:{dealId}
                ORDER_QTY_PREFIX                   // KEYS[3]: order:qty:
        );

        Object[] args = new Object[]{
                String.valueOf(stockQuantity),     // ARGV[1]: stockQuantity
                orderId,                           // ARGV[2]: orderId
                String.valueOf(requestQuantity),   // ARGV[3]: requestQty
                String.valueOf(expiredAt),         // ARGV[4]: expiredAt
                String.valueOf(currentTime)        // ARGV[5]: currentTime
        };

        return redisRepository.executeScript(reserveStockScript, keys, args);
    }

    private DefaultRedisScript<Long> loadReserveStockScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("lua/reserve-deal-stock.lua"));
        script.setResultType(Long.class);
        return script;
    }
}
