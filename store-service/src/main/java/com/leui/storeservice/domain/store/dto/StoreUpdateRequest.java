package com.leui.storeservice.domain.store.dto;

import java.time.LocalTime;

public record StoreUpdateRequest(
        String name,
        String phoneNumber,
        LocalTime closedAt
) {
}
