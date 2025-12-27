package com.leui.storeservice.domain.store.dto;

import com.leui.storeservice.domain.store.entity.Stores;

import java.time.LocalDateTime;

public record StoreInfoResponse(
        Long id,
        String name,
        Double x,
        Double y,
        String address,
        String phoneNumber,
        LocalDateTime closedAt
) {

    public static StoreInfoResponse from(Stores store) {
        return new StoreInfoResponse(
                store.getId(),
                store.getName(),
                store.getLocation().getX(),
                store.getLocation().getY(),
                store.getAddress(),
                store.getPhoneNumber(),
                store.getClosedAt()
        );
    }

}
