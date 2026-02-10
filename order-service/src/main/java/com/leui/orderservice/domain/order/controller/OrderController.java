package com.leui.orderservice.domain.order.controller;

import com.leui.orderservice.domain.order.dto.OrderCancelRequest;
import com.leui.orderservice.domain.order.dto.OrderCreateRequest;
import com.leui.orderservice.domain.order.dto.OrderDetailResponse;
import com.leui.orderservice.domain.order.dto.OrderCancelResponse;
import com.leui.orderservice.domain.order.service.OrderService;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.global.facade.OrderPaymentProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService ordersService;
    private final OrderPaymentProviderService orderPaymentProviderService;

    @PostMapping
    public ResponseEntity<PaymentReadyResponse> startOrderTransaction(
            @Valid @RequestBody OrderCreateRequest request,
            @RequestHeader("x-user-id") Long userId
    ) {
        return ResponseEntity.ok(orderPaymentProviderService.startOrderTransaction(request, userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@PathVariable Long orderId) {
        return ResponseEntity.ok(ordersService.getOrderDetail(orderId));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderCancelResponse> cancelOrder(
            @PathVariable String orderId,
            @RequestHeader("x-user-id") Long userId,
            @RequestBody OrderCancelRequest request
    ) {
        return ResponseEntity.ok(orderPaymentProviderService.cancel(orderId, userId, request));
    }

}
