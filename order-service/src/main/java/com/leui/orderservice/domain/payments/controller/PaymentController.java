package com.leui.orderservice.domain.payments.controller;

import com.leui.orderservice.domain.payments.dto.PaymentFailParam;
import com.leui.orderservice.domain.payments.dto.PaymentStatusResponse;
import com.leui.orderservice.domain.payments.provider.ApproveResult;
import com.leui.orderservice.global.facade.OrderPaymentProviderService;
import com.leui.orderservice.domain.payments.dto.provider.KakaoSuccessParam;
import dto.payment.PaymentFailResponse;
import com.leui.orderservice.domain.payments.dto.provider.TossSuccessParam;
import enumtype.PaymentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final OrderPaymentProviderService orderPaymentProviderService;

    /**
     * Client Polling API
     * @param orderId 주문 고유 id
     * @return 결제 상태 (PAYMENT_DONE,
     */
    @GetMapping("/{orderId}/status")
    public ResponseEntity<PaymentStatusResponse> status(@PathVariable String orderId) {
        return ResponseEntity.ok(orderPaymentProviderService.status(orderId));
    }

    @PostMapping("/toss/success")
    public ResponseEntity<ApproveResult> confirmToss(@RequestBody TossSuccessParam param) {
        return ResponseEntity.ok(orderPaymentProviderService.approvePayments(PaymentProvider.TOSS, param));
    }

    @PostMapping("/kakao/success")
    public ResponseEntity<ApproveResult> confirmKakao(@RequestBody KakaoSuccessParam param) {
        return ResponseEntity.ok(orderPaymentProviderService.approvePayments(PaymentProvider.KAKAO, param));
    }

    @PostMapping("/fail")
    public ResponseEntity<PaymentFailResponse> fail(@RequestBody PaymentFailParam param) {
        return ResponseEntity.ok(orderPaymentProviderService.failPayment(param));
    }

}
