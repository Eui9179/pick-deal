package com.leui.storeservice.domain.store.dto;

import java.time.LocalDateTime;

public record StoreUpdateRequest(
        String name,
        String phoneNumber,
        LocalDateTime closedAt
) {
}
