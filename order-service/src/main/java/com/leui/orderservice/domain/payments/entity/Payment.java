package com.leui.orderservice.domain.payments.entity;

import com.leui.orderservice.domain.order.entity.Order;
import com.leui.orderservice.global.entity.BaseEntity;
import enumtype.PaymentProvider;
import enumtype.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Payment extends BaseEntity {

    @Id
    @Column(name = "order_id")
    private String orderId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    /**
     * Provider별 고유 키
     * - Kakao: tid (ready에서 받음)
     * - Toss: paymentKey (confirm에서 받음)
     */
    private String paymentKey;

    private BigDecimal amount;

    private String method;

    private LocalDateTime approvedAt;

    public Payment(Order order, PaymentProvider provider) {
        this.order = order;
        this.orderId = order.getId();
        this.provider = provider;
        this.status = PaymentStatus.READY;
    }

    public void updatePaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
    }

    public void completePayment(BigDecimal amount, String method) {
        this.status = PaymentStatus.DONE;
        this.amount = amount;
        this.method = method;
        this.approvedAt = LocalDateTime.now();
    }

    public void fail() {
        this.status = PaymentStatus.FAILED;
    }

    public void cancel() {
        this.status = PaymentStatus.CANCELLED;
    }
}
