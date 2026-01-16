package com.leui.orderservice.domain.payments.dto.provider;

import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.entity.PaymentProvider;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TossReadyPayload extends PaymentReadyResponse {
    private String orderId;
    private String successUrl;
    private String failUrl;
    private String customerEmail;
    private String customerName;

    public TossReadyPayload(String orderId, String successUrl, String failUrl, String customerEmail, String customerName) {
        super(PaymentProvider.TOSS);
        this.orderId = orderId;
        this.successUrl = successUrl;
        this.failUrl = failUrl;
        this.customerEmail = customerEmail;
        this.customerName = customerName;
    }
}