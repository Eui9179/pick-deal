package com.leui.orderservice.domain.payments.dto.provider;

public record KakaoCancelRequest(
        String cid,
        String tid,
        Integer cancelAmount,
        Integer cancelTaxFreeAmount
) {
}
