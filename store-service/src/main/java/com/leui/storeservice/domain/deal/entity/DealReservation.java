package com.leui.storeservice.domain.deal.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class DealReservation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long dealId;

    private String orderId;

    private Long userId;

    private long expiredAt;

    public DealReservation(Long dealId, String orderId, Long userId, long expiredAt) {
        this.dealId = dealId;
        this.orderId = orderId;
        this.userId = userId;
        this.expiredAt = expiredAt;
    }
}
