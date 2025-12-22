package com.leui.dealservice.dto;

public record DealsRequest(
        Double longitude,
        Double latitude,
        String category,
        int radius
) {
}
