package com.leui.orderservice.domain.payments.provider.kakao.feignclient;

import com.leui.orderservice.domain.payments.dto.provider.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
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
    KakaoApproveResponse approve(
            @RequestHeader("Authorization") String authorization,
            @RequestBody KakaoApproveRequest request
    );

    @PostMapping("/online/v1/payment/cancel")
    ResponseEntity<Void> cancel(
            @RequestHeader("Authorization") String authorization,
            @RequestBody KakaoCancelRequest request
    );

}
