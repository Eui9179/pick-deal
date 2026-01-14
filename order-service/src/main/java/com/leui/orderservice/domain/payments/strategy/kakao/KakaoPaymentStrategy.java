package com.leui.orderservice.domain.payments.strategy.kakao;

import com.leui.orderservice.domain.payments.dto.PaymentReadyPayload;
import com.leui.orderservice.domain.payments.entity.PaymentProvider;
import com.leui.orderservice.domain.payments.strategy.PaymentStrategy;

public class KakaoPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentReadyPayload ready() {
        return null;
    }

    @Override
    public PaymentProvider support() {
        return PaymentProvider.KAKAO;
    }
}
