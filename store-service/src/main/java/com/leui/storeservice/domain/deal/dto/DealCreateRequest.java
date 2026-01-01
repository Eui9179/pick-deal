package com.leui.storeservice.domain.deal.dto;

import com.leui.storeservice.domain.discountpolicy.dto.DiscountPolicyCreateRequest;
import com.leui.storeservice.domain.discountpolicy.entity.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DealCreateRequest(
        Long categoryId,
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        LocalDateTime pickupEndTime,
        DiscountPolicyCreateRequest policyCreateRequest
) {
}
