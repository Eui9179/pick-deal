package com.leui.orderservice.domain.payments.dto;

import com.leui.orderservice.domain.payments.entity.PaymentProvider;

public record PaymentReadyRequest(
        PaymentProvider provider
) {
}
