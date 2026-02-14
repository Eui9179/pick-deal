package com.leui.storeservice.domain.deal.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@SQLDelete(sql = "UPDATE deal_reservation SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class DealReservation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long dealId;

    private String orderId;

    private Long userId;

    private int quantity;

    private long expiredAt;

    private LocalDateTime deletedAt;

    public DealReservation(Long dealId, String orderId, Long userId, int quantity, long expiredAt) {
        this.dealId = dealId;
        this.orderId = orderId;
        this.userId = userId;
        this.quantity = quantity;
        this.expiredAt = expiredAt;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
