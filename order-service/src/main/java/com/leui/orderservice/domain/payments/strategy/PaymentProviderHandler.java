package com.leui.orderservice.domain.payments.strategy;

import com.leui.orderservice.domain.payments.dto.PaymentConfirmRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.entity.PaymentProvider;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PaymentProviderHandler {

    Map<PaymentProvider, PaymentStrategy> strategeis;

    public PaymentProviderHandler(List<PaymentStrategy> strategyList) {
        this.strategeis = strategyList.stream()
                .collect(Collectors.toMap(
                        PaymentStrategy::support,
                        Function.identity()
                ));
    }

    public PaymentReadyResponse readey(PaymentReadyRequest request) {
        return strategeis.get(request.provider())
                .ready(request);
    }

    public ConfirmResult confirm(PaymentConfirmRequest request) {
        PaymentStrategy paymentStrategy = strategeis.get(request.provider());
        return paymentStrategy.confirm(request);
    }

}
