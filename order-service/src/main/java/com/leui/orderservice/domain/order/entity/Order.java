package com.leui.orderservice.domain.order.entity;

import com.leui.orderservice.global.entity.BaseEntity;
import enumtype.OrderStatus;
import enumtype.PaymentProvider;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Order extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(updatable = false)
    private Long userId;

    @Column(updatable = false)
    private Long dealId;

    private int quantity;

    @Setter
    private PaymentProvider provider;

    @Setter
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Setter
    private BigDecimal totalAmount;

    @Setter
    private String paymentKey;

    @Setter
    private LocalDateTime approvedAt;

    @Setter
    private String failDescription;

    private LocalDateTime pickupTime;

    public Order(Long userId, Long dealId, LocalDateTime pickupTime, int quantity, PaymentProvider provider, BigDecimal totalAmount) {
        this.userId = userId;
        this.dealId = dealId;
        this.pickupTime = pickupTime;
        this.quantity = quantity;
        this.provider = provider;
        this.status = OrderStatus.ORDER_START;
        this.totalAmount = totalAmount;
    }

    public void setErrorStatus(OrderStatus status, String failDescription) {
        this.status = status;
        this.failDescription = failDescription;
    }

    public void updatePaymentDone() {
        this.status = OrderStatus.PAYMENT_DONE;
    }

    public void updatePaymentFail() {
        this.status = OrderStatus.PAYMENT_FAILED;
    }

    public void updatePaymentCancel() {
        this.status = OrderStatus.PAYMENT_CANCEL;
    }

    public void updateOrderCreated() {
        this.status = OrderStatus.ORDER_CANCELED;
    }

    public void updateOrderExpired() {
        this.status = OrderStatus.ORDER_EXPIRED;
    }
}
