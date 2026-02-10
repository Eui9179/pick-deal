package com.leui.orderservice.domain.payments.provider;

import enumtype.OrderStatus;
import enumtype.PaymentProvider;

public record ConfirmResult (
        PaymentProvider provider,
        OrderStatus status,
        String failCode
) {
}
