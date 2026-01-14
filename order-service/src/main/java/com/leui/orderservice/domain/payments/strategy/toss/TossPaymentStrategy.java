package com.leui.orderservice.domain.payments.strategy.toss;

import com.leui.orderservice.domain.payments.dto.PaymentReadyPayload;
import com.leui.orderservice.domain.payments.entity.PaymentProvider;
import com.leui.orderservice.domain.payments.strategy.PaymentStrategy;

public class TossPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentReadyPayload ready() {
        return null;
    }

    @Override
    public PaymentProvider support() {
        return PaymentProvider.TOSS;
    }
}
