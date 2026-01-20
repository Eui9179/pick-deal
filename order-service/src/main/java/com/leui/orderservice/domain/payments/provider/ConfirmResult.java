package com.leui.orderservice.domain.payments.provider;

import enumtype.PaymentProvider;
import enumtype.ConfirmStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmResult {
    private PaymentProvider provider;
    private ConfirmStatus status;

    public static ConfirmResult from(PaymentProvider provider, String status) {
        return switch (provider) {
            case TOSS -> new ConfirmResult(provider, ConfirmStatus.from(status));
            case KAKAO -> new ConfirmResult(provider, ConfirmStatus.from(status));
        };
    }
}
