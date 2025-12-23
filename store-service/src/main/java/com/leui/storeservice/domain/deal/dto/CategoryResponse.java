package com.leui.storeservice.domain.deal.dto;

import com.leui.storeservice.domain.deal.entity.Category;

public record CategoryResponse(Long id, String name) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }

}
