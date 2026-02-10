package com.leui.orderservice.domain.payments.provider;

import com.leui.orderservice.domain.order.entity.Order;
import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import dto.payment.PaymentSuccessParam;
import enumtype.PaymentProvider;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PaymentProviderHandler {

    private final Map<PaymentProvider, PaymentStrategy> strategies;

    public PaymentProviderHandler(List<PaymentStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        PaymentStrategy::support,
                        Function.identity()
                ));
    }

    @Transactional
    public PaymentReadyResponse ready(PaymentReadyRequest readyRequest) {
        return strategies.get(readyRequest.provider())
                .ready(readyRequest);
    }

    @Transactional
    public ConfirmResult approve(PaymentProvider provider, PaymentSuccessParam param, Order order) {
        return strategies.get(provider)
                .approve(param, order);
    }

}
