package com.leui.orderservice.domain.payments.controller;

import com.leui.orderservice.domain.payments.dto.PaymentConfirmResponse;
import com.leui.orderservice.domain.payments.dto.TossConfirmRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments/toss")
public class TossPaymentController {

    @GetMapping("/confirm")
    public ResponseEntity<PaymentConfirmResponse> confirm(@ModelAttribute TossConfirmRequest request) {
        return ResponseEntity.ok().build();
    }
}
