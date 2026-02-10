package com.leui.orderservice.domain.payments.provider;

import enumtype.OrderStatus;
import enumtype.PaymentProvider;

public record ApproveResult(
        PaymentProvider provider,
        OrderStatus status,
        String failCode
) {
}
