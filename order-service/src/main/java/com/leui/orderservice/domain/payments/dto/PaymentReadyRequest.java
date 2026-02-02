package com.leui.orderservice.domain.payments.dto;

import com.leui.orderservice.domain.order.entity.Order;
import enumtype.PaymentProvider;

import java.math.BigDecimal;

public record PaymentReadyRequest(
        PaymentProvider provider,
        Order order,
        Long userId,
        String dealName,
        int quantity,
        BigDecimal totalAmount
) {
}
