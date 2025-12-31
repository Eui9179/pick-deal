package com.leui.storeservice.domain.deal.dto;

import com.leui.storeservice.domain.deal.entity.Deal;
import com.leui.storeservice.domain.deal.entity.DealStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DealDetailResponse(
        Long id,
        Long storeId,
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        DealStatus dealStatus,
        LocalDateTime pickupEndTime
) {

    public static DealDetailResponse from(Deal deal) {
        return new DealDetailResponse(
                deal.getId(),
                deal.getStore().getId(),
                deal.getName(),
                deal.getDescription(),
                deal.getPrice(),
                deal.getStockQuantity(),
                deal.getDealStatus(),
                deal.getPickupEndTime()
        );
    }

}

