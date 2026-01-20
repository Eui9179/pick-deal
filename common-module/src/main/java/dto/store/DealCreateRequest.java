package dto.store;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DealCreateRequest(
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMddHHmmss")
        LocalDateTime pickupEndTime,
        DiscountPolicyCreateRequest discountPolicy
) {
}
