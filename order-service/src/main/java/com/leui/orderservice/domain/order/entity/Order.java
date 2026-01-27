package com.leui.orderservice.domain.order.entity;

import com.leui.orderservice.domain.payments.entity.Payment;
import com.leui.orderservice.global.entity.BaseEntity;
import enumtype.OrderStatus;
import enumtype.PaymentProvider;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Order extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

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
    private String failDescription;

    public Order(Long userId, Long dealId, int quantity, PaymentProvider provider) {
        this.userId = userId;
        this.dealId = dealId;
        this.quantity = quantity;
        this.provider = provider;
        this.status = OrderStatus.ORDER_START;
    }

    public void setErrorStatus(OrderStatus status, String failDescription) {
        this.status = status;
        this.failDescription = failDescription;
    }
}
