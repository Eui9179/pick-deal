package com.leui.orderservice.domain.order.controller;

import com.leui.orderservice.domain.order.service.OrderService;
import enumtype.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/internal/v1/order")
public class OrderInternalController {

    private final OrderService orderService;

    @PostMapping("/{orderId}/ready-for-pickup")
    public ResponseEntity<Void> updateStatusReadyForPickup(@PathVariable String orderId) {
        orderService.updateStatus(orderId, OrderStatus.ORDER_READY_FOR_PICKUP);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/complete")
    public ResponseEntity<Void> updateStatusUpdate(@PathVariable String orderId) {
        orderService.updateStatus(orderId, OrderStatus.ORDER_COMPLETE);
        return ResponseEntity.ok().build();
    }
}
