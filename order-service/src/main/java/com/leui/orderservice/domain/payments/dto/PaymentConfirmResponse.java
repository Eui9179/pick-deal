package com.leui.orderservice.domain.payments.dto;

import com.leui.orderservice.domain.payments.entity.PaymentProvider;
import lombok.Getter;

@Getter
public record PaymentConfirmResponse(PaymentProvider provider) {
}
