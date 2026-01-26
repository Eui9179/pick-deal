package com.leui.orderservice.domain.payments.dto.provider;

public record KakaoConfirmResponse(
        String aid,
        String tid,
        String cid,
        String partner_order_id,
        String partner_user_id,
        String item_name,
        Integer quantity,
        Amount amount,
        String created_at,
        String approved_at
) {
    record Amount(
            Integer total,
            Integer tax_free,
            Integer vat,
            Integer point,
            Integer discount
    ) {
    }
}
