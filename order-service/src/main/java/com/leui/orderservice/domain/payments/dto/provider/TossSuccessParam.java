package com.leui.orderservice.domain.payments.dto.provider;

import dto.payment.PaymentSuccessParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TossSuccessParam implements PaymentSuccessParam {
    private String paymentType;
    private String orderId;
    private String paymentKey;
    private BigDecimal amount;
}
