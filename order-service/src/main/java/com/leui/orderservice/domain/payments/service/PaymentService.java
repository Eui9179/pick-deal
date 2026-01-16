package com.leui.orderservice.domain.payments.service;

import com.leui.orderservice.domain.payments.dto.PaymentConfirmResponse;
import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.entity.PaymentProvider;
import com.leui.orderservice.domain.payments.strategy.ConfirmResult;
import com.leui.orderservice.domain.payments.strategy.PaymentProviderHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentProviderHandler paymentProviderHandler;

    public PaymentReadyResponse readyPayment(PaymentReadyRequest request) {
        return paymentProviderHandler.readey(request);
    }

    public PaymentConfirmResponse confirmPayment(PaymentProvider provider, Map<String, Object> request) {
        ConfirmResult result = paymentProviderHandler.confirm(provider, request);
        return new PaymentConfirmResponse(result.getProvider()); // TODO 리턴 데이터 처리
    }
}
