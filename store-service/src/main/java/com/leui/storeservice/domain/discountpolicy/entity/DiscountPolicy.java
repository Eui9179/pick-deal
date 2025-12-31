package com.leui.storeservice.domain.discountpolicy.entity;

import com.leui.storeservice.common.entity.BaseEntity;
import com.leui.storeservice.domain.deal.entity.Deal;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class DiscountPolicy extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, updatable = false)
    private Deal deal;

    @Setter
    @Column(nullable = false)
    private int discountIntervalMinutes;

    @Setter
    @Column(nullable = false, precision = 5, scale = 4) // 0.0000 ~ 0.9999 허용
    private BigDecimal maxDiscountRate;

    @Setter
    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal discountRatePerInterval;

    public DiscountPolicy(Deal deal, int discountIntervalMinutes, BigDecimal maxDiscountRate, BigDecimal discountRatePerInterval) {
        this.deal = deal;
        this.discountIntervalMinutes = discountIntervalMinutes;
        this.maxDiscountRate = maxDiscountRate;
        this.discountRatePerInterval = discountRatePerInterval;
    }
}
