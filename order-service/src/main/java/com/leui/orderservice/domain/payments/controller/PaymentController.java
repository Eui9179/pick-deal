package com.leui.orderservice.domain.payments.controller;

import com.leui.orderservice.domain.facade.OrderPaymentService;
import com.leui.orderservice.domain.payments.provider.ConfirmResult;
import dto.payment.TossSuccessParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final OrderPaymentService orderPaymentService;

    @GetMapping("/toss/success")
    public ResponseEntity<ConfirmResult> confirm(@ModelAttribute TossSuccessParam param) {
        return ResponseEntity.ok(orderPaymentService.confirmToss(param));
    }

}
