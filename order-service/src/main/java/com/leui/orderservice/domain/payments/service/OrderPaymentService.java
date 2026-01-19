package com.leui.orderservice.domain.payments.service;

import com.leui.orderservice.domain.payments.dto.PaymentConfirmResponse;
import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import enumtype.PaymentProvider;
import com.leui.orderservice.domain.payments.provider.ConfirmResult;
import com.leui.orderservice.domain.payments.provider.PaymentProviderHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class OrderPaymentService {

    private final PaymentProviderHandler paymentProviderHandler;

    public PaymentReadyResponse readyPayment(PaymentReadyRequest request, Long userId) {
        return paymentProviderHandler.ready(request, userId);
    }

    public ConfirmResult confirmPayment(PaymentProvider provider, Map<String, Object> param) {
        return paymentProviderHandler.confirm(provider, param);
    }
}
