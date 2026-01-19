package com.leui.orderservice.domain.payments.dto;

import enumtype.PaymentProvider;

import java.math.BigDecimal;

public record PaymentReadyRequest(
        String orderId,
        Long dealId,
        BigDecimal amount,
        PaymentProvider provider
) {
}
