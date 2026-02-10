package com.leui.orderservice.domain.payments.dto.provider;

import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import enumtype.PaymentProvider;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoReadyPayload extends PaymentReadyResponse {
    private String tid;
    private String nextRedirectMobileUrl;
    private String nextRedirectAppUrl;
    private String nextRedirectPcUrl;

    public KakaoReadyPayload(String orderId, String tid, String nextRedirectMobileUrl, String nextRedirectPcUrl) {
        super(orderId, PaymentProvider.KAKAO);
        this.tid = tid;
        this.nextRedirectMobileUrl = nextRedirectMobileUrl;
        this.nextRedirectPcUrl = nextRedirectPcUrl;
    }

}

