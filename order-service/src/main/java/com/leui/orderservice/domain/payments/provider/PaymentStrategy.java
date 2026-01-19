package com.leui.orderservice.domain.payments.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leui.orderservice.domain.order.dto.OrderCreateRequest;
import com.leui.orderservice.domain.payments.dto.PaymentFailPayload;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import enumtype.PaymentProvider;

import java.util.Map;

public interface PaymentStrategy<T> {
    PaymentReadyResponse ready(OrderCreateRequest request, String orderId, Long userId);
    ConfirmResult confirm(T param);
    PaymentFailPayload fail();
    PaymentProvider support();
    Class<T> paramType();

    default ConfirmResult confirm(ObjectMapper mapper, Map<String, Object> param) {
        T dto = mapper.convertValue(param, paramType());
        return confirm(dto);
    }
}
