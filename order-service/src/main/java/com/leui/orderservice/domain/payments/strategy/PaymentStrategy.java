package com.leui.orderservice.domain.payments.strategy;

import com.leui.orderservice.domain.payments.dto.*;
import com.leui.orderservice.domain.payments.entity.PaymentProvider;

public interface PaymentStrategy {
    PaymentReadyResponse ready(PaymentReadyRequest request);
    ConfirmResult confirm(PaymentConfirmRequest request);
    PaymentFailPayload fail();
    PaymentProvider support();
}
