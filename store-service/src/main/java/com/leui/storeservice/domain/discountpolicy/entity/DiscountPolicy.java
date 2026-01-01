package com.leui.storeservice.domain.discountpolicy.entity;

import com.leui.storeservice.common.entity.BaseEntity;
import com.leui.storeservice.domain.deal.entity.Deal;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class DiscountPolicy extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deal_id", nullable = false, updatable = false)
    private Deal deal;

    @Setter
    @Column(nullable = false)
    private LocalDateTime startAt;

    @Setter
    @Column(nullable = false)
    private int discountIntervalMinutes;

    @Setter
    @Column(nullable = false, precision = 4, scale = 3)
    private BigDecimal maxDiscountValue;

    @Setter
    @Column(nullable = false)
    private BigDecimal discountValue;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    public DiscountPolicy(Deal deal, LocalDateTime startAt, int discountIntervalMinutes, BigDecimal maxDiscountValue,
                          BigDecimal discountValue, DiscountType discountType) {
        this.deal = deal;
        this.startAt = startAt;
        this.discountIntervalMinutes = discountIntervalMinutes;
        this.maxDiscountValue = maxDiscountValue;
        this.discountValue = discountValue;
        this.discountType = discountType;
    }
}
