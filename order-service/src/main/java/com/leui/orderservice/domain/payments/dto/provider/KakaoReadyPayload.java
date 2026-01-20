package com.leui.orderservice.domain.payments.dto.provider;

import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import enumtype.PaymentProvider;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoReadyPayload extends PaymentReadyResponse {
    private String tid;
    private String nextRedirectAppUrl;
    private String nextRedirectMobileUrl;
    private String nextRedirectPcUrl;
    private String androidAppScheme;
    private String iosAppScheme;

    public KakaoReadyPayload(String tid, String nextRedirectAppUrl, String nextRedirectMobileUrl,
                             String nextRedirectPcUrl, String androidAppScheme, String iosAppScheme) {
        super(PaymentProvider.KAKAO);
        this.tid = tid;
        this.nextRedirectAppUrl = nextRedirectAppUrl;
        this.nextRedirectMobileUrl = nextRedirectMobileUrl;
        this.nextRedirectPcUrl = nextRedirectPcUrl;
        this.androidAppScheme = androidAppScheme;
        this.iosAppScheme = iosAppScheme;
    }

}

