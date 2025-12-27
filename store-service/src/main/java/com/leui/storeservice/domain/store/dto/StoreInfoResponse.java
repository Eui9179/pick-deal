package com.leui.storeservice.domain.store.dto;

import com.leui.storeservice.domain.store.entity.Stores;

import java.time.LocalDateTime;

public record StoreInfoResponse(
        Long id,
        String name,
        Double longitude,
        Double latitude,
        String address,
        String phoneNumber,
        LocalDateTime closedAt
) {

    public static StoreInfoResponse from(Stores store) {
        return new StoreInfoResponse(
                store.getId(),
                store.getName(),
                store.getPosition().getLongitude(),
                store.getPosition().getLatitude(),
                store.getAddress(),
                store.getPhoneNumber(),
                store.getClosedAt()
        );
    }


}
