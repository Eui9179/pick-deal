package com.leui.orderservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {
    private Long storeId;
    private Long productId;
    private int quantity;
    LocalDateTime pickupTime;
}
