package com.leui.storeservice.domain.store.dto;

import java.time.LocalDateTime;

public record StoreSaveRequest(
        String name,
        Double x,
        Double y,
        String address,
        String phoneNumber,
        LocalDateTime closedAt
) {
}
