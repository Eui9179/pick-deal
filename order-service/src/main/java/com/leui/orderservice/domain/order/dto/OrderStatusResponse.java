package com.leui.orderservice.domain.order.dto;

import com.leui.orderservice.domain.order.entity.OrderStatus;

public record OrderStatusResponse(Long orderId, OrderStatus status) {
}

