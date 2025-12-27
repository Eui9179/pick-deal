package com.leui.storeservice.domain.deal.dto;

public record DealsRequest(
        Double longitude,
        Double latitude,
        String category,
        int radius
) {
}
