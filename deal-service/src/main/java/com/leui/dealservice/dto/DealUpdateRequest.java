package com.leui.dealservice.dto;

import java.time.LocalDateTime;

public record DealUpdateRequest(
        Long dealId,
        Long categoryId,
        String name,
        String description,
        int price,
        int discountPrice,
        int stockQuantity,
        LocalDateTime pickupEndTime
) {
}