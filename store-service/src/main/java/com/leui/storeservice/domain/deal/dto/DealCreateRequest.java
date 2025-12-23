package com.leui.storeservice.domain.deal.dto;

import java.time.LocalDateTime;

public record DealCreateRequest(
        Long storeId,
        Long categoryId,
        String name,
        String description,
        int price,
        int discountPrice,
        int stockQuantity,
        LocalDateTime pickupEndTime
) {
}
