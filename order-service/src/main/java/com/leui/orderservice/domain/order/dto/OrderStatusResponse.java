package com.leui.orderservice.domain.order.dto;

import enumtype.OrderStatus;

public record OrderStatusResponse(Long orderId, OrderStatus status) {
}

