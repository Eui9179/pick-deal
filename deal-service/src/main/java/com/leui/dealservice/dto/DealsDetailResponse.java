package com.leui.dealservice.dto;

import com.leui.dealservice.entity.Deals;
import com.leui.dealservice.entity.DealsStatus;

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
                deals.getStoreId(),
                deals.getName(),
                deals.getDescription(),
                CategoryResponse.from(deals.getCategory()),
                deals.getPrice(),
                deals.getDiscountPrice(),
                deals.getStockQuantity(),
                deals.getDealsStatus(),
                deals.getPickupEndTime()
        );
    }

}

