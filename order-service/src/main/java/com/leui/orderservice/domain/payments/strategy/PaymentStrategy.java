package com.leui.orderservice.domain.payments.strategy;

import com.leui.orderservice.domain.payments.dto.PaymentConfirmPayload;
import com.leui.orderservice.domain.payments.dto.PaymentFailPayload;
import com.leui.orderservice.domain.payments.dto.PaymentReadyPayload;
import com.leui.orderservice.domain.payments.dto.PaymentSuccessPayload;
import com.leui.orderservice.domain.payments.entity.PaymentProvider;

public interface PaymentStrategy {
    PaymentReadyPayload ready();
    PaymentConfirmPayload confirm();
    PaymentSuccessPayload success();
    PaymentFailPayload fail();
    PaymentProvider support();
}
