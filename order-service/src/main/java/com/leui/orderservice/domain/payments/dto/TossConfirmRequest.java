package com.leui.orderservice.domain.payments.dto;

import java.math.BigDecimal;

public record TossConfirmRequest(
        String paymentKey,
        String orderId,
        BigDecimal amount
) {
}
