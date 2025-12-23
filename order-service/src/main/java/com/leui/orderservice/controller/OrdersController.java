package com.leui.orderservice.controller;

import com.leui.orderservice.dto.OrderCreateRequest;
import com.leui.orderservice.dto.OrderCreateResponse;
import com.leui.orderservice.dto.OrderDetailResponse;
import com.leui.orderservice.dto.OrderStatusResponse;
import com.leui.orderservice.service.OrdersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrdersController {

    private final OrdersService ordersService;

    @PostMapping
    public ResponseEntity<OrderCreateResponse> createOrder(@Valid @RequestBody OrderCreateRequest orderCreateRequest) {
        return ResponseEntity.ok(ordersService.createOrder(orderCreateRequest));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@PathVariable Long orderId) {
        return ResponseEntity.ok(ordersService.getOrderDetail(orderId));
    }

    @GetMapping("/{orderId}/status")
    public ResponseEntity<OrderStatusResponse> getOrderStatus(@PathVariable Long orderId) {
        return ResponseEntity.ok(ordersService.getOrderStatus(orderId));
    }

    @PostMapping(value = "/{orderId}/paid")
    public ResponseEntity<OrderStatusResponse> updateOrderStatusPaid(@PathVariable Long orderId) {
        return ResponseEntity.ok(ordersService.updateOrderStatusPaid(orderId));
    }
}
