package com.leui.dealservice.dto;

import com.leui.dealservice.entity.DealsStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DealCreateRequest {
    private Long storeId;
    private String name;
    private String description;
    private String category;
    private int price;
    private int discountPrice;
    private int stockQuantity;
    private LocalDateTime pickupEndTime;
}
