package com.leui.orderservice.domain.payments.controller;

import com.leui.orderservice.domain.payments.dto.PaymentConfirmResponse;
import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.entity.PaymentProvider;
import com.leui.orderservice.domain.payments.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentsController {

    private final PaymentService paymentService;

    @PostMapping("/ready")
    public ResponseEntity<PaymentReadyResponse> ready(@RequestBody PaymentReadyRequest request) {
        return ResponseEntity.ok(paymentService.readyPayment(request));
    }

    @GetMapping("/toss/confirm")
    public ResponseEntity<PaymentConfirmResponse> confirm(@ModelAttribute Map<String, Object> request) {
        return ResponseEntity.ok(paymentService.confirmPayment(PaymentProvider.TOSS, request));
    }

}
