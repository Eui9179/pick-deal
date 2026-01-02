package com.leui.storeservice.domain.deal.dto;

import com.leui.storeservice.domain.discountpolicy.dto.DiscountPolicyCreateRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DealCreateRequest(
        String name,
        String description,
        BigDecimal price,
        int stockQuantity,
        LocalDateTime pickupEndTime,
        DiscountPolicyCreateRequest discountPolicy
) {
}
