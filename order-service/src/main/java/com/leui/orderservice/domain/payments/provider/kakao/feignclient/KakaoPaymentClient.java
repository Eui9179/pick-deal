package com.leui.orderservice.domain.payments.provider.kakao.feignclient;

import com.leui.orderservice.domain.payments.dto.provider.KakaoConfirmRequest;
import com.leui.orderservice.domain.payments.dto.provider.KakaoConfirmResponse;
import com.leui.orderservice.domain.payments.dto.provider.KakaoReadyPayload;
import com.leui.orderservice.domain.payments.dto.provider.KakaoReadyRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakaoPaymentClient", url = "https://open-api.kakaopay.com")
public interface KakaoPaymentClient {

    @PostMapping("/online/v1/payment/ready")
    KakaoReadyPayload ready(
            @RequestHeader("Authorization") String authorization,
            @RequestBody KakaoReadyRequest request
    );

    @PostMapping("/online/v1/payment/approve")
    KakaoConfirmResponse confirm(
            @RequestHeader("Authorization") String authorization,
            @RequestBody KakaoConfirmRequest request
    );
}
