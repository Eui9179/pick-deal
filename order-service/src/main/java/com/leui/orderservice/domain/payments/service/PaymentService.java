package com.leui.orderservice.domain.payments.service;

import com.leui.orderservice.domain.payments.dto.PaymentConfirmRequest;
import com.leui.orderservice.domain.payments.dto.PaymentConfirmResponse;
import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.strategy.ConfirmResult;
import com.leui.orderservice.domain.payments.strategy.PaymentProviderHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentProviderHandler paymentProviderHandler;

    public PaymentReadyResponse readyPayment(PaymentReadyRequest request) {
        return paymentProviderHandler.readey(request);
    }

    public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest request) {
        ConfirmResult result = paymentProviderHandler.confirm(request);
        return new PaymentConfirmResponse(result.getProvider());
    }
}
