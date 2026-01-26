package com.leui.orderservice.domain.payments.dto.provider;

public record KakaoConfirmRequest(
        String cid,
        String tid,
        String partner_order_id,
        String partner_user_id,
        String pg_token
) {
}
