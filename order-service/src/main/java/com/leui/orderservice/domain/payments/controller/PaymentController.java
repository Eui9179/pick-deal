package com.leui.orderservice.domain.payments.controller;

import com.leui.orderservice.domain.payments.provider.ConfirmResult;
import com.leui.orderservice.domain.facade.OrderPaymentService;
import enumtype.PaymentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final OrderPaymentService orderPaymentService;

    @GetMapping("/toss/success")
    public ResponseEntity<ConfirmResult> confirm(@ModelAttribute Map<String, Object> param) {
        return ResponseEntity.ok(orderPaymentService.confirmPayment(PaymentProvider.TOSS, param));
    }

}
