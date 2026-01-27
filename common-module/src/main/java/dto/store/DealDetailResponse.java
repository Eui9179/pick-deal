package dto.store;


import enumtype.DealStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DealDetailResponse(
        Long id,
        Long storeId,
        String name,
        String description,
        BigDecimal price,
        BigDecimal discountPrice,
        BigDecimal discountValue,
        long stockQuantity,
        DealStatus dealStatus,
        LocalDateTime pickupEndTime
) {

    public static DealDetailResponse from(
            Long dealId,
            Long storeId,
            String name,
            String description,
            BigDecimal price,
            BigDecimal discountPrice,
            BigDecimal discountValue,
            long stockQuantity,
            DealStatus dealStatus,
            LocalDateTime pickupEndTime
    ) {
        return new DealDetailResponse(
                dealId,
                storeId,
                name,
                description,
                price,
                discountPrice,
                discountValue,
                stockQuantity,
                dealStatus,
                pickupEndTime
        );
    }

}

