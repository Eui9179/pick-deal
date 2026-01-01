package com.leui.storeservice.domain.discountpolicy.calculator;

import com.leui.storeservice.common.util.LocationUtils;
import com.leui.storeservice.domain.deal.entity.Deal;
import com.leui.storeservice.domain.deal.entity.DealStatus;
import com.leui.storeservice.domain.discountpolicy.entity.DiscountPolicy;
import com.leui.storeservice.domain.discountpolicy.entity.DiscountType;
import com.leui.storeservice.domain.store.entity.Store;
import com.leui.storeservice.domain.store.entity.StoreCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class DiscountCalculatorTest {

    DiscountCalculator discountCalculator;

    Store store;

    @BeforeEach
    void setUp() {
        discountCalculator = new DiscountCalculator();
        store = new Store(
                "name",
                LocationUtils.createPoint(0.0, 0.0),
                "address",
                "phoneNumber",
                LocalDateTime.now(),
                new StoreCategory("code", "desc")
        );
    }

    @Test
    @DisplayName("Amount Type 10000원 / 3000원 할인 -> 7000원 테스트")
    void testCalculateAmount() {
        //given
        BigDecimal originPrice = BigDecimal.valueOf(100_00);
        BigDecimal maxDiscountValue = BigDecimal.valueOf(3_000);
        BigDecimal discountAmount = BigDecimal.valueOf(1000);

        LocalDateTime current = LocalDateTime.now();
        LocalDateTime discountStartAt = current.minusMinutes(30);

        DiscountType type = DiscountType.AMOUNT;

        Deal deal = new Deal(
                store,
                "name",
                "description",
                originPrice,
                10,
                DealStatus.ON_SALE,
                current
        );

        DiscountPolicy policy = new DiscountPolicy(
                deal,
                discountStartAt,
                10,
                maxDiscountValue,
                discountAmount,
                type
        );

        //when
        BigDecimal discountPrice = discountCalculator.calculate(deal);

        //then
        assertThat(discountPrice).isEqualTo(BigDecimal.valueOf(7000));
    }

    @Test
    @DisplayName("Amount Type 최대 할인 3000원, 10000원 / 5000원 할인 -> 7000원 테스트 (3000원 고정)")
    void testMaxDiscountAmount() {
        //given
        BigDecimal originPrice = BigDecimal.valueOf(100_00);
        BigDecimal maxDiscountValue = BigDecimal.valueOf(3_000);
        BigDecimal discountAmount = BigDecimal.valueOf(1000);

        LocalDateTime current = LocalDateTime.now();
        LocalDateTime discountStartAt = current.minusMinutes(50);

        DiscountType type = DiscountType.AMOUNT;

        Deal deal = new Deal(
                store,
                "name",
                "description",
                originPrice,
                10,
                DealStatus.ON_SALE,
                current
        );

        DiscountPolicy policy = new DiscountPolicy(
                deal,
                discountStartAt,
                10,
                maxDiscountValue,
                discountAmount,
                type
        );

        //when
        BigDecimal discountPrice = discountCalculator.calculate(deal);

        //then
        assertThat(discountPrice).isEqualTo(BigDecimal.valueOf(7000));
    }

    @Test
    @DisplayName("Percent Type 10000원 / 30% 할인 -> 7000원 테스트")
    void testCalculatePercent() {
        //given
        BigDecimal originPrice = BigDecimal.valueOf(100_00);
        BigDecimal maxDiscountValue = BigDecimal.valueOf(0.3);
        BigDecimal discountAmount = BigDecimal.valueOf(0.1);

        LocalDateTime current = LocalDateTime.now();
        LocalDateTime discountStartAt = current.minusMinutes(30);

        DiscountType type = DiscountType.PERCENT;

        Deal deal = new Deal(
                store,
                "name",
                "description",
                originPrice,
                10,
                DealStatus.ON_SALE,
                current
        );

        DiscountPolicy policy = new DiscountPolicy(
                deal,
                discountStartAt,
                10,
                maxDiscountValue,
                discountAmount,
                type
        );

        //when
        BigDecimal discountPrice = discountCalculator.calculate(deal);

        //then
        BigDecimal then = new BigDecimal("7000");
        assertThat(discountPrice.doubleValue()).isEqualTo(then.doubleValue());
    }

    @Test
    @DisplayName("Percent 최대 할인 30%, 10000원 / 50% 할인 -> 7000원 테스트 (30% 고정)")
    void testMaxDiscountPercent() {
        //given
        BigDecimal originPrice = BigDecimal.valueOf(100_00);
        BigDecimal maxDiscountValue = BigDecimal.valueOf(0.3);
        BigDecimal discountAmount = BigDecimal.valueOf(0.1);

        int discountIntervalMinutes = 1;
        int intervalMinutes = 5;

        LocalDateTime current = LocalDateTime.now();
        LocalDateTime discountStartAt = current.minusMinutes(intervalMinutes);

        DiscountType type = DiscountType.PERCENT;

        Deal deal = new Deal(
                store,
                "name",
                "description",
                originPrice,
                10,
                DealStatus.ON_SALE,
                current
        );

        DiscountPolicy policy = new DiscountPolicy(
                deal,
                discountStartAt,
                discountIntervalMinutes,
                maxDiscountValue,
                discountAmount,
                type
        );

        //when
        BigDecimal discountPrice = discountCalculator.calculate(deal);

        //then
        BigDecimal then = new BigDecimal("7000");
        assertThat(discountPrice.doubleValue()).isEqualTo(then.doubleValue());
    }

}
