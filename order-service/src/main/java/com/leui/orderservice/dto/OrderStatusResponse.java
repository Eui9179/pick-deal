package com.leui.orderservice.dto;

import com.leui.orderservice.entity.OrderStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderStatusResponse {
    private Long orderId;
    private OrderStatus status;
}
