package com.leui.orderservice.domain.payments.provider;

import com.leui.orderservice.domain.order.dto.OrderCancelRequest;
import com.leui.orderservice.domain.order.dto.OrderCancelResponse;
import com.leui.orderservice.domain.order.entity.Order;
import com.leui.orderservice.domain.payments.dto.ApproveResult;
import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import dto.payment.PaymentSuccessParam;
import enumtype.PaymentProvider;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PaymentProviderHandler {

    private final Map<PaymentProvider, PaymentStrategy> strategies;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentProviderHandler(KafkaTemplate<String, Object> kafkaTemplate, List<PaymentStrategy> strategyList) {
        this.kafkaTemplate = kafkaTemplate;
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

    public ApproveResult approve(PaymentProvider provider, PaymentSuccessParam param, Order order) {
        return strategies.get(provider)
                .approve(param, order);
    }

    public OrderCancelResponse cancel(Order order, OrderCancelRequest request) {
        return strategies.get(order.getProvider())
                .cancel(order, request);
    }
}
