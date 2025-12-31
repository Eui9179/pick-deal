package com.leui.storeservice.domain.deal.dto;

import com.leui.storeservice.domain.deal.entity.DealStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DealUpdateRequest(
        Long dealId,
        Long categoryId,
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        LocalDateTime pickupEndTime,
        DealStatus dealStatus
) {
}