package com.leui.storeservice.domain.store.dto;

public record StoresRequest(
        Double x,
        Double y,
        int radius
) {
}
