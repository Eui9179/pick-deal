package com.leui.orderservice.domain.payments.dto;

import enumtype.PaymentProvider;
import lombok.Getter;

@Getter
public record PaymentConfirmResponse(PaymentProvider provider) {
}
