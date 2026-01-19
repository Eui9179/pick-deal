package com.leui.orderservice.domain.order.entity;

import enumtype.PaymentProvider;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(updatable = false)
    private Long userId;

    @Column(updatable = false)
    private Long dealId;

    @Setter
    private PaymentProvider provider;
}
