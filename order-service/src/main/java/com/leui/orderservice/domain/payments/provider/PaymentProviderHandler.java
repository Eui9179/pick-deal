package com.leui.orderservice.domain.payments.provider;

import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.entity.PaymentProvider;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PaymentProviderHandler {

    private final Map<PaymentProvider, PaymentStrategy> strategeis;

    public PaymentProviderHandler(List<PaymentStrategy> strategyList) {
        this.strategeis = strategyList.stream()
                .collect(Collectors.toMap(
                        PaymentStrategy::support,
                        Function.identity()
                ));
    }

    public PaymentReadyResponse ready(PaymentReadyRequest request, Long userId) {
        return strategeis.get(request.provider())
                .ready(request, userId);
    }

    public ConfirmResult confirm(PaymentProvider provider, Map<String, Object> request) {
        return strategeis.get(provider)
                .confirm(request);
    }

}
