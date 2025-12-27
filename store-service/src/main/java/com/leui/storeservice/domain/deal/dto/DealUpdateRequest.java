package com.leui.storeservice.domain.deal.dto;

import com.leui.storeservice.domain.deal.entity.DealsStatus;

import java.time.LocalDateTime;

public record DealUpdateRequest(
        Long dealId,
        Long categoryId,
        String name,
        String description,
        int price,
        int discountPrice,
        int stockQuantity,
        LocalDateTime pickupEndTime,
        DealsStatus dealsStatus
) {
}