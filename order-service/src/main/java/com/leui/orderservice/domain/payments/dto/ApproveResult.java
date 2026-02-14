package com.leui.orderservice.domain.payments.dto;

import enumtype.OrderStatus;
import enumtype.PaymentProvider;

public record ApproveResult(
        PaymentProvider provider,
        OrderStatus status,
        String failCode
) {
}
