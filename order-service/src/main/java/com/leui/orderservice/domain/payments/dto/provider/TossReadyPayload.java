package com.leui.orderservice.domain.payments.dto.provider;

import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import enumtype.PaymentProvider;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TossReadyPayload extends PaymentReadyResponse {
    private String successUrl;
    private String failUrl;
    private String customerEmail;
    private String customerName;

    public TossReadyPayload(String orderId, String successUrl, String failUrl, String customerEmail, String customerName) {
        super(orderId, PaymentProvider.TOSS);
        this.successUrl = successUrl;
        this.failUrl = failUrl;
        this.customerEmail = customerEmail;
        this.customerName = customerName;
    }
}