package com.leui.orderservice.domain.payments.provider.toss.feignclient;

import com.leui.orderservice.domain.payments.dto.provider.TossConfirmResponse;
import dto.payment.TossSuccessParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "tossPaymentClient", url = "https://api.tosspayments.com")
public interface TossPaymentClient {

    @PostMapping("/v1/payments/confirm")
    TossConfirmResponse confirmPayment(
            @RequestHeader("Authorization") String authorization,
            @RequestBody TossSuccessParam body
    );

}
