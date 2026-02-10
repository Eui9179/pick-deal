package com.leui.orderservice.domain.payments.provider;

import com.leui.orderservice.domain.order.entity.Order;
import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import dto.payment.PaymentSuccessParam;
import enumtype.PaymentProvider;

public interface PaymentStrategy {
    PaymentReadyResponse ready(PaymentReadyRequest request);

    ConfirmResult approve(PaymentSuccessParam param, Order order);

    PaymentProvider support();

}
