package com.leui.storeservice.domain.discountpolicy.calculator;

import com.leui.storeservice.domain.deal.entity.Deal;
import com.leui.storeservice.domain.discountpolicy.entity.DiscountPolicy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class DiscountCalculator {

    public BigDecimal calculate(Deal deal, DiscountPolicy policy) {
        long discountCount = calculateDiscountCount(policy.getStartAt(), policy.getDiscountIntervalMinutes());

        if (discountCount < 0) return deal.getPrice();

        return switch (policy.getDiscountType()) {
            case PERCENT -> calculatePercent(deal.getPrice(), policy, discountCount);
            case AMOUNT -> calculateAmount(deal.getPrice(), policy, discountCount);
        };
    }

    private BigDecimal calculatePercent(BigDecimal originPrice, DiscountPolicy discountPolicy, long discountCount) {
        BigDecimal discountPercent = discountPolicy.getDiscountValue()
                .multiply(BigDecimal.valueOf(discountCount));

        if (discountPercent.compareTo(discountPolicy.getMaxDiscountValue()) > 0){
            // 최고 할인율 적용
            BigDecimal applyPercent = BigDecimal.ONE.subtract(discountPolicy.getMaxDiscountValue());
            return originPrice.multiply(applyPercent);
        }

        BigDecimal applyPercent = BigDecimal.ONE.subtract(discountPercent);

        return originPrice.multiply(applyPercent);
    }

    private BigDecimal calculateAmount(BigDecimal originPrice, DiscountPolicy discountPolicy, long discountCount) {
        BigDecimal discountAmount = discountPolicy.getDiscountValue().multiply(BigDecimal.valueOf(discountCount));
        BigDecimal maxDiscountAmount = discountPolicy.getMaxDiscountValue();

        if (discountAmount.compareTo(maxDiscountAmount) > 0)
            return originPrice.subtract(maxDiscountAmount);

        return originPrice.subtract(discountAmount);
    }

    /**
     * 할인 횟수 계산
     * @param startAt 할인 시작 시간
     * @param intervalMinutes 할인 주기
     * @return 할인 횟수
     */
    private long calculateDiscountCount(LocalDateTime startAt, int intervalMinutes) {
        LocalDateTime now = LocalDateTime.now();
        if (startAt.isAfter(now)) {
            return -1;
        }
        long minutes = Duration.between(startAt, now).toMinutes();
        if (minutes < intervalMinutes) return -1;
        return minutes / intervalMinutes;
    }

}
