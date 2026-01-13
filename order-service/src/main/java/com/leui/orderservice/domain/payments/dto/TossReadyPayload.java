package com.leui.orderservice.domain.payments.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TossReadyPayload extends PaymentReadyPayload {
    private String orderId;
    private String successUrl;
    private String failUrl;
    private String customerEmail;
    private String customerName;
}