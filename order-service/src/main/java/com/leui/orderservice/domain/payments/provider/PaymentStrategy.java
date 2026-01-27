package com.leui.orderservice.domain.payments.provider;

import com.leui.orderservice.domain.order.dto.OrderCreateRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import dto.payment.PaymentSuccessParam;
import enumtype.PaymentProvider;

public interface PaymentStrategy<T extends PaymentSuccessParam> {
    PaymentReadyResponse ready(OrderCreateRequest request, String orderId, Long userId);

    ConfirmResult confirmPay(T param);

    PaymentProvider support();

    Class<T> type();

    @SuppressWarnings("unchecked")
    default ConfirmResult confirm(PaymentSuccessParam param) {
        if (!type().isInstance(param)) {
            throw new IllegalArgumentException(
                    "Invalid SuccessParam. expected = " + type().getSimpleName()
                            + ", actual = " + param.getClass().getSimpleName());
        }
        return confirmPay((T) param);
    }

}
