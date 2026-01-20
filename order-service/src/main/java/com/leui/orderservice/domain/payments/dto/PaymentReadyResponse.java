package com.leui.orderservice.domain.payments.dto;

import enumtype.PaymentProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class PaymentReadyResponse {
    private final PaymentProvider provider;
}
