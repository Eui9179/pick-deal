package com.leui.orderservice.domain.payments.strategy.toss;

import com.leui.orderservice.domain.payments.dto.PaymentConfirmPayload;
import com.leui.orderservice.domain.payments.dto.PaymentFailPayload;
import com.leui.orderservice.domain.payments.dto.PaymentReadyPayload;
import com.leui.orderservice.domain.payments.dto.PaymentSuccessPayload;
import com.leui.orderservice.domain.payments.entity.PaymentProvider;
import com.leui.orderservice.domain.payments.strategy.PaymentStrategy;

public class TossPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentReadyPayload ready() { // 상품 price, 상품 id, 주문자 id
        // 주문 가격 검증
        // 상품 이름
        // 주문자 이름
        return null;
    }

    @Override
    public PaymentConfirmPayload confirm() {
        return null;
    }

    @Override
    public PaymentSuccessPayload success() {
        return null;
    }

    @Override
    public PaymentFailPayload fail() {
        return null;
    }

    @Override
    public PaymentProvider support() {
        return PaymentProvider.TOSS;
    }
}
