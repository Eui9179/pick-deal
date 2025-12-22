package com.leui.dealservice.dto;

import com.leui.dealservice.entity.Deals;
import com.leui.dealservice.entity.DealsStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DealsDetailResponse {
    private Long id;
    private Long storeId;
    private String name;
    private String description;
    private String category;
    private int price;
    private int discountPrice;
    private int stockQuantity;
    private DealsStatus dealsStatus;
    private LocalDateTime pickupEndTime;

    public static DealsDetailResponse from(Deals deals) {
        return new DealsDetailResponse(
                deals.getId(),
                deals.getStoreId(),
                deals.getName(),
                deals.getDescription(),
                deals.getCategory().getName(),
                deals.getPrice(),
                deals.getDiscountPrice(),
                deals.getStockQuantity(),
                deals.getDealsStatus(),
                deals.getPickupEndTime()
        );
    }
}
