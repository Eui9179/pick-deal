package com.leui.orderservice.domain.payments.provider;

import com.leui.orderservice.domain.order.entity.Order;
import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import dto.payment.PaymentSuccessParam;
import enumtype.PaymentProvider;

public interface PaymentStrategy<T extends PaymentSuccessParam> {
    PaymentReadyResponse ready(PaymentReadyRequest request);

    ConfirmResult confirmPay(T param, Order order);

    PaymentProvider support();

    Class<T> type();

    @SuppressWarnings("unchecked")
    default ConfirmResult confirm(PaymentSuccessParam param, Order order) {
        if (!type().isInstance(param)) {
            throw new IllegalArgumentException(
                    "Invalid SuccessParam. expected = " + type().getSimpleName()
                            + ", actual = " + param.getClass().getSimpleName());
        }
        return confirmPay((T) param, order);
    }

}
