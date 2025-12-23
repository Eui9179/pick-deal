package com.leui.orderservice.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreateRequest {
    @NotNull
    private Long storeId;

    @NotNull
    private Long productId;

    @Min(value = 1, message = "Quantity must be at least one.")
    private int quantity;

    @NotNull
    @FutureOrPresent(message = "PickupTime must be set to the future or present.")
    private LocalDateTime pickupTime;
}
