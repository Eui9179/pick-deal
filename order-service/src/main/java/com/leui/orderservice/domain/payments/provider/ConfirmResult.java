package com.leui.orderservice.domain.payments.provider;

import enumtype.OrderStatus;
import enumtype.PaymentProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmResult {
    private PaymentProvider provider;
    private OrderStatus status;
}
