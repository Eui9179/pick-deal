package dto.store;


import enumtype.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DiscountPolicyCreateRequest(
        DiscountType discountType,
        BigDecimal discountValue,
        BigDecimal maxDiscountValue,
        int discountIntervalMinutes,
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMddHHmmss")
        LocalDateTime startAt
) {
}
