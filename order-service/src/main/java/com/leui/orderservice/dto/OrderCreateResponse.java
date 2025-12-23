package com.leui.orderservice.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreateResponse {
    private Long orderId;
}
