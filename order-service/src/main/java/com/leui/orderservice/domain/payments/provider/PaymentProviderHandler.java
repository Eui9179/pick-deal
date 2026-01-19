package com.leui.orderservice.domain.payments.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leui.orderservice.domain.order.dto.OrderCreateRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import enumtype.PaymentProvider;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PaymentProviderHandler {

    private final Map<PaymentProvider, PaymentStrategy<?>> strategeis;

    private final ObjectMapper objectMapper;

    public PaymentProviderHandler(ObjectMapper objectMapper, List<PaymentStrategy<?>> strategyList) {
        this.objectMapper = objectMapper;
        this.strategeis = strategyList.stream()
                .collect(Collectors.toMap(
                        PaymentStrategy::support,
                        Function.identity()
                ));
    }

    public PaymentReadyResponse ready(OrderCreateRequest request, String orderId, Long userId) {
        return strategeis.get(request.provider())
                .ready(request, orderId, userId);
    }

    public ConfirmResult confirm(PaymentProvider provider, Map<String, Object> param) {
        return strategeis.get(provider)
                .confirm(objectMapper, param);
    }

}
