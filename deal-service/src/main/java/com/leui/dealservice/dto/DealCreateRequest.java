package com.leui.dealservice.dto;

import com.leui.dealservice.entity.DealsStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DealCreateRequest {
    private Long storeId;
    private Long categoryId;
    private String name;
    private String description;
    private int price;
    private int discountPrice;
    private int stockQuantity;
    private LocalDateTime pickupEndTime;
}
