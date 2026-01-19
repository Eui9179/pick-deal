package com.leui.orderservice.domain.payments.controller;

import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.provider.ConfirmResult;
import com.leui.orderservice.domain.payments.service.OrderPaymentService;
import enumtype.PaymentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final OrderPaymentService orderPaymentService;

    @PostMapping("/ready")
    public ResponseEntity<PaymentReadyResponse> ready(
            @RequestBody PaymentReadyRequest request,
            @RequestHeader("x-user-id") Long userId
    ) {
        return ResponseEntity.ok(orderPaymentService.readyPayment(request, userId));
    }

    @GetMapping("/toss/success")
    public ResponseEntity<ConfirmResult> confirm(@ModelAttribute Map<String, Object> param) {
        return ResponseEntity.ok(orderPaymentService.confirmPayment(PaymentProvider.TOSS, param));
    }

}
