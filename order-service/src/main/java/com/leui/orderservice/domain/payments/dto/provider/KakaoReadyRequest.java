package com.leui.orderservice.domain.payments.dto.provider;

import java.math.BigDecimal;

public record KakaoReadyRequest(
        String cid,
        String orderId,
        String userId,
        String dealName,
        long quantity,
        BigDecimal totalAmount,
        String successUrl,
        String cancelUrl,
        String failUrl
) {
}
