package com.leui.orderservice.domain.payments.dto.provider;

public record KakaoConfirmResponse(
        String aid,
        String tid,
        String cid,
        String partnerOrderId,
        String partnerUserId,
        String itemName,
        Integer quantity,
        Amount amount,
        String createdAt,
        String approvedAt
) {
    public record Amount(
            Integer total,
            Integer taxFree,
            Integer vat,
            Integer point,
            Integer discount
    ) {
    }
}

