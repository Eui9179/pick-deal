package com.leui.orderservice.domain.order.dto;

import enumtype.OrderStatus;

public record OrderCancelResponse(
        OrderStatus orderStatus
) {
}
