package com.leui.orderservice.domain.payments.dto;

import com.leui.orderservice.domain.payments.entity.PaymentProvider;

import java.math.BigDecimal;

public record PaymentConfirmRequest(
        PaymentProvider provider,
        String paymentKey,
        String orderId,
        BigDecimal amount
) {

}
