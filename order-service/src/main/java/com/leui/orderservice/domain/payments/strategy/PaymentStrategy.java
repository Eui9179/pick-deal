package com.leui.orderservice.domain.payments.strategy;

import com.leui.orderservice.domain.payments.dto.PaymentReadyPayload;
import com.leui.orderservice.domain.payments.entity.PaymentProvider;

public interface PaymentStrategy {
    PaymentReadyPayload ready();
    PaymentProvider support();
}
