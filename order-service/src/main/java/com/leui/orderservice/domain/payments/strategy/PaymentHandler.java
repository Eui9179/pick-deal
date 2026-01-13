package com.leui.orderservice.domain.payments.strategy;

import com.leui.orderservice.domain.payments.dto.PaymentReadyPayload;
import com.leui.orderservice.domain.payments.entity.PaymentProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PaymentHandler {

    Map<PaymentProvider, PaymentStrategy> strategeis;

    public PaymentHandler(List<PaymentStrategy> strategyList) {
        this.strategeis = strategyList.stream()
                .collect(Collectors.toMap(
                        PaymentStrategy::support,
                        Function.identity()
                ));
    }

    public PaymentReadyPayload readey(PaymentProvider provider) {
        return strategeis.get(provider).ready();
    }

}
