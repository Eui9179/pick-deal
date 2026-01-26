package com.leui.orderservice.domain.payments.provider.kakao.strategy;

import com.leui.orderservice.domain.order.dto.OrderCreateRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.provider.ConfirmResult;
import com.leui.orderservice.domain.payments.provider.PaymentStrategy;
import com.leui.orderservice.domain.payments.provider.kakao.feignclient.KakaoPaymentClient;
import dto.payment.PaymentSuccessParam;
import enumtype.PaymentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KakaoPaymentStrategy implements PaymentStrategy {

    private final KakaoPaymentClient kakaoPaymentClient;

    @Override
    public PaymentReadyResponse ready(OrderCreateRequest request, String orderId, Long userId) {
//        kakaoPaymentClient.ready()
        return null;
    }

    @Override
    public ConfirmResult confirmPay(PaymentSuccessParam param) {
//        kakaoPaymentClient.confirm()
        return null;
    }

    @Override
    public PaymentProvider support() {
        return null;
    }

    @Override
    public Class type() {
        return null;
    }
}
