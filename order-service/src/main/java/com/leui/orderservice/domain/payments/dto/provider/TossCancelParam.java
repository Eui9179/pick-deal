package com.leui.orderservice.domain.payments.dto.provider;

public record TossCancelParam(
        String cancelReason,
        String cancelAmount
        ) {
}
