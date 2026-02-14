package com.leui.orderservice.domain.payments.provider;

import com.leui.orderservice.domain.order.dto.OrderCancelRequest;
import com.leui.orderservice.domain.order.dto.OrderCancelResponse;
import com.leui.orderservice.domain.order.entity.Order;
import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import dto.payment.PaymentSuccessParam;
import enumtype.OrderStatus;
import enumtype.PaymentProvider;
import kafka.event.PaymentDoneEvent;
import kafka.topic.EventTopics;
import org.springframework.scheduling.annotation.Async;
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


    @Async
    public void approve(PaymentProvider provider, PaymentSuccessParam param, Order order) {
        strategies.get(provider)
                .approve(param, order);

        if (result.status() == OrderStatus.PAYMENT_DONE) {
            order.updatePaymentDone();
            kafkaTemplate.send(EventTopics.PAYMENT_DONE, order.getId(),
                    PaymentDoneEvent.builder()
                            .orderId(order.getId())
                            .dealId(order.getDealId())
                            .quantity(order.getQuantity())
                            .build());
        } else {
            failPayment(order, result.failCode());
        }

    }

    public OrderCancelResponse cancel(Order order, OrderCancelRequest request) {
        return strategies.get(order.getProvider())
                .cancel(order, request);
    }
}
