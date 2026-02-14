package com.leui.orderservice.domain.payments.dto;

import enumtype.PaymentProvider;

public record PaymentFailParam(
        String orderId,
        String failCode,
        PaymentProvider provider
) {
}
