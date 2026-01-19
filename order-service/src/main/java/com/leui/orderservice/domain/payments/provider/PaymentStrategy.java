package com.leui.orderservice.domain.payments.provider;

import com.leui.orderservice.domain.payments.dto.PaymentFailPayload;
import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.entity.PaymentProvider;

import java.util.Map;

public interface PaymentStrategy {
    PaymentReadyResponse ready(PaymentReadyRequest request, Long userId);
    ConfirmResult confirm(Map<String, Object> request);
    PaymentFailPayload fail();
    PaymentProvider support();
}
