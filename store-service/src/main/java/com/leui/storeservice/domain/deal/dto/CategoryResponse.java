package com.leui.storeservice.domain.deal.dto;

import com.leui.storeservice.domain.deal.entity.DealCategory;

public record CategoryResponse(Long id, String name) {

    public static CategoryResponse from(DealCategory dealCategory) {
        return new CategoryResponse(dealCategory.getId(), dealCategory.getName());
    }

}
