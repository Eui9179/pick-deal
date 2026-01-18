package com.leui.orderservice.domain.payments.provider.toss.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TossConfirmResponse(
        String lastTransactionKey,
        String paymentKey,
        String orderId,
        String orderName,
        String status,
        LocalDateTime approvedAt,
        Card card,
        String country,
        String failure,
        BigDecimal totalAmount,
        BigDecimal balanceAmount,
        BigDecimal suppliedAmount,
        BigDecimal vat,
        BigDecimal taxFreeAmount,
        String method
) {
    private record Card(
            String issuerCode,
            String number,
            String approveNo,
            String cardType,
            String ownerType,
            String acquireStatus,
            Boolean useCardPoint,
            BigDecimal amount
    ) {
    }
}