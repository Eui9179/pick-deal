package com.leui.orderservice.domain.order.dto;

public record OrderCancelRequest(
        String cancelReason
) {
}
