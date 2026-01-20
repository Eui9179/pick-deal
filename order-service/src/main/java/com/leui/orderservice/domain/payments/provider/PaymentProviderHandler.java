package com.leui.orderservice.domain.payments.provider;

import com.leui.orderservice.domain.order.dto.OrderCreateRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import dto.payment.PaymentSuccessParam;
import enumtype.PaymentProvider;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PaymentProviderHandler {

    private final Map<PaymentProvider, PaymentStrategy<?>> strategies;

    public PaymentProviderHandler(List<PaymentStrategy<?>> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        PaymentStrategy::support,
                        Function.identity()
                ));
    }

    public PaymentReadyResponse ready(
            OrderCreateRequest request,
            String orderId,
            Long userId
    ) {
        return strategies.get(request.provider())
                .ready(request, orderId, userId);
    }

    public ConfirmResult confirm(PaymentProvider provider, PaymentSuccessParam param) {
        return strategies.get(provider)
                .confirm(param);
    }

}
