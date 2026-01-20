package dto.store;


import enumtype.DealStatus;

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