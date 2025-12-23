package com.leui.orderservice.dto;

import com.leui.orderservice.entity.OrderStatus;

public record OrderStatusResponse(Long orderId, OrderStatus status) {
}

