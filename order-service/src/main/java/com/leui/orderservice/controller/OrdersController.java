package com.leui.orderservice.controller;

import com.leui.orderservice.dto.OrderRequest;
import com.leui.orderservice.dto.OrderResponse;
import com.leui.orderservice.service.OrdersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrdersController {

    private final OrdersService ordersService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<OrderResponse> order(@RequestBody OrderRequest orderRequest) {
        return ResponseEntity.ok(ordersService.createOrder(orderRequest));
    }
}
