package com.leui.orderservice.domain.payments.strategy;

import com.leui.orderservice.domain.payments.entity.PaymentProvider;
import com.leui.orderservice.domain.payments.strategy.toss.TossConfirmResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ConfirmResult {
    private PaymentProvider provider;

    public static ConfirmResult from(TossConfirmResponse tossConfirmResponse) {
        return null;
    }
}
