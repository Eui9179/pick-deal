package com.leui.orderservice.domain.payments.dto;

import com.leui.orderservice.domain.payments.entity.PaymentProvider;

public record PaymentReadyResponse(
        PaymentProvider provider,
        PaymentReadyPayload payload
) {
}
