package com.leui.orderservice.domain.order.controller;

import com.leui.orderservice.domain.order.dto.OrderCreateRequest;
import com.leui.orderservice.domain.order.dto.OrderDetailResponse;
import com.leui.orderservice.domain.order.service.OrderService;
import com.leui.orderservice.global.facade.OrderPaymentProviderService;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

}
