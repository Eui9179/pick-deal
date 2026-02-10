package com.leui.orderservice.domain.payments.controller;

import com.leui.orderservice.domain.payments.provider.ConfirmResult;
import com.leui.orderservice.global.facade.OrderPaymentProviderService;
import dto.payment.KakaoSuccessParam;
import com.leui.orderservice.domain.payments.dto.PaymentFailParam;
import dto.payment.PaymentFailResponse;
import dto.payment.TossSuccessParam;
import enumtype.OrderStatus;
import enumtype.PaymentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final OrderPaymentProviderService orderPaymentProviderService;

    @GetMapping("/{orderId}/status")
    public ResponseEntity<OrderStatus> status(@PathVariable String orderId) {
        return ResponseEntity.ok(OrderStatus.ORDER_START);
    }

    @PostMapping("/toss/success")
    public ResponseEntity<ConfirmResult> confirmToss(@RequestBody TossSuccessParam param) {
        return ResponseEntity.ok(orderPaymentProviderService.approvePayments(PaymentProvider.TOSS, param));
    }

    @PostMapping("/kakao/success")
    public ResponseEntity<ConfirmResult> confirmKakao(@RequestBody KakaoSuccessParam param) {
        return ResponseEntity.ok(orderPaymentProviderService.approvePayments(PaymentProvider.KAKAO, param));
    }

    @PostMapping("/fail")
    public ResponseEntity<PaymentFailResponse> fail(@RequestBody PaymentFailParam param) {
        return ResponseEntity.ok(orderPaymentProviderService.failPayment(param));
    }

}
