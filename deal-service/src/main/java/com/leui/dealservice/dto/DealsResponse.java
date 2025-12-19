package com.leui.dealservice.dto;

import com.leui.dealservice.entity.Deals;
import com.leui.dealservice.entity.DealsStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DealsResponse {
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

    public static DealsResponse from(Deals deals) {
        return DealsResponse.builder()
                .id(deals.getId())
                .storeId(deals.getStoreId())
                .name(deals.getName())
                .description(deals.getDescription())
                .category(deals.getCategory().getName())
                .price(deals.getPrice())
                .discountPrice(deals.getDiscountPrice())
                .stockQuantity(deals.getStockQuantity())
                .dealsStatus(deals.getDealsStatus())
                .pickupEndTime(deals.getPickupEndTime())
                .build();
    }
}
