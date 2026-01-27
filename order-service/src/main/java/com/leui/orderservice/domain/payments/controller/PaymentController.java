package com.leui.orderservice.domain.payments.controller;

import com.leui.orderservice.global.facade.OrderPaymentService;
import com.leui.orderservice.domain.payments.provider.ConfirmResult;
import dto.payment.KakaoSuccessParam;
import dto.payment.PaymentFailRequest;
import dto.payment.PaymentFailResponse;
import dto.payment.TossSuccessParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final OrderPaymentService orderPaymentService;

    @PostMapping("/toss/confirm")
    public ResponseEntity<ConfirmResult> confirmToss(@RequestBody TossSuccessParam param) {
        return ResponseEntity.ok(orderPaymentService.confirmToss(param));
    }

    @PostMapping("/kakao/confirm")
    public ResponseEntity<ConfirmResult> confirmKakao(@RequestBody KakaoSuccessParam param) {
        return ResponseEntity.ok(orderPaymentService.confirmKakao(param));
    }

    @PostMapping("/fail")
    public ResponseEntity<PaymentFailResponse> fail(@RequestBody PaymentFailRequest param) {
        return ResponseEntity.ok(orderPaymentService.failPayment(param));
    }
}
