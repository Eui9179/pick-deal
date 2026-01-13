package com.leui.orderservice.domain.payments.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class KakaoReadyPayload {
    String tid;
    String nextRedirectAppUrl;
    String nextRedirectMobileUrl;
    String nextRedirectPcUrl;
    String androidAppScheme;
    String iosAppScheme;
}

