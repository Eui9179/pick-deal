package com.leui.orderservice.domain.payments.service;

import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.strategy.PaymentHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentHandler paymentHandler;

    public PaymentReadyResponse readyPayment(PaymentReadyRequest request) {
        return new PaymentReadyResponse(request.provider(), paymentHandler.readey(request.provider()));
    }
}
