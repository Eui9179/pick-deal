package com.leui.orderservice.domain.payments.dto;

import enumtype.OrderStatus;

public record PaymentStatusResponse(
        String orderId,
        OrderStatus orderStatus,
        String failCode
) {
}
