package com.leui.orderservice.controller;

import com.leui.orderservice.dto.OrderCreateRequest;
import com.leui.orderservice.dto.OrderCreateResponse;
import com.leui.orderservice.dto.OrderDetailResponse;
import com.leui.orderservice.dto.OrderStatusResponse;
import com.leui.orderservice.service.OrdersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrdersController {

    private final OrdersService ordersService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<OrderCreateResponse> createOrder(@Valid @RequestBody OrderCreateRequest orderCreateRequest) {
        return ResponseEntity.ok(ordersService.createOrder(orderCreateRequest));
    }

    @GetMapping(value = "/{orderId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@PathVariable Long orderId) {
        return ResponseEntity.ok(ordersService.getOrderDetail(orderId));
    }

    @GetMapping(value = "/{orderId}/status",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderStatusResponse> getOrderStatus(@PathVariable Long orderId) {
        return ResponseEntity.ok(ordersService.getOrderStatus(orderId));
    }

    @PostMapping(value = "/{orderId}/paid",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderStatusResponse> updateOrderStatusPaid(@PathVariable Long orderId) {
        return ResponseEntity.ok(ordersService.updateOrderStatusPaid(orderId));
    }
}
