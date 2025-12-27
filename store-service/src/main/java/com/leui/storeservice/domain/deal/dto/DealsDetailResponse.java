package com.leui.storeservice.domain.deal.dto;

import com.leui.storeservice.domain.deal.entity.Deals;
import com.leui.storeservice.domain.deal.entity.DealsStatus;

import java.time.LocalDateTime;

public record DealsDetailResponse(
        Long id,
        Long storeId,
        String name,
        String description,
        CategoryResponse category,
        int price,
        int discountPrice,
        int stockQuantity,
        DealsStatus dealsStatus,
        LocalDateTime pickupEndTime
) {

    public static DealsDetailResponse from(Deals deals) {
        return new DealsDetailResponse(
                deals.getId(),
                deals.getStore().getId(),
                deals.getName(),
                deals.getDescription(),
                CategoryResponse.from(deals.getDealCategory()),
                deals.getPrice(),
                deals.getDiscountPrice(),
                deals.getStockQuantity(),
                deals.getDealsStatus(),
                deals.getPickupEndTime()
        );
    }

}

