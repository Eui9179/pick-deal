package com.leui.storeservice.domain.store.dto;

public record StoreFindRequest(
        Double x,
        Double y,
        int radius
) {
}
