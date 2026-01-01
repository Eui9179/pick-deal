package com.leui.storeservice.domain.discountpolicy.dto;

import com.leui.storeservice.domain.discountpolicy.entity.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DiscountPolicyCreateRequest(
        DiscountType discountType,
        BigDecimal discountValue,
        BigDecimal maxDiscountValue,
        int discountIntervalMinutes,
        LocalDateTime startAt
) {
}
