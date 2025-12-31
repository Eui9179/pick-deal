package com.leui.storeservice.domain.deal.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DealCreateRequest(
        Long categoryId,
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        LocalDateTime pickupEndTime
) {
}
