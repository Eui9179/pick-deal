package com.leui.storeservice.domain.store.dto;

public record StoresRequest(
        Double longitude,
        Double latitude,
        int radius
) {
}
