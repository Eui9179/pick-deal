package com.leui.orderservice.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record OrderCreateRequest(
        @NotNull
        Long storeId,

        @NotNull
        Long productId,

        @Min(value = 1, message = "Quantity must be at least one.")
        int quantity,

        @NotNull
        @FutureOrPresent
        LocalDateTime pickupTime
) {
}
