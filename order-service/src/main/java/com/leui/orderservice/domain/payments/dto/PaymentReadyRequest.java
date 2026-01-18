package com.leui.orderservice.domain.payments.dto;

import com.leui.orderservice.domain.payments.entity.PaymentProvider;

import java.math.BigDecimal;

public record PaymentReadyRequest(
        Long dealId,
        BigDecimal amount,
        PaymentProvider provider
) {
}
