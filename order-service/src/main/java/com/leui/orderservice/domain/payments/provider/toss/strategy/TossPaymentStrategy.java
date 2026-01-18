package com.leui.orderservice.domain.payments.provider.toss.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leui.orderservice.domain.global.feignclient.StoreDealFeignClient;
import com.leui.orderservice.domain.payments.dto.PaymentFailPayload;
import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.entity.PaymentProvider;
import com.leui.orderservice.domain.payments.provider.toss.dto.TossConfirmResponse;
import com.leui.orderservice.domain.payments.provider.toss.feignclient.TossPaymentClient;
import com.leui.orderservice.domain.payments.provider.ConfirmResult;
import com.leui.orderservice.domain.payments.provider.PaymentStrategy;
import dto.store.DealDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class TossPaymentStrategy implements PaymentStrategy {

    @Value("${toss.secret-key}")
    private String secretKey;

    private final TossPaymentClient tossPaymentClient;

    private StoreDealFeignClient feignClient;


    @Override
    public PaymentReadyResponse ready(PaymentReadyRequest request) {
        // 주문 가격 검증
        // 상품 이름
        DealDetailResponse dealDetail = feignClient.getDealDetail(request.dealId());
        // TODO
        // 주문자 이름
        return null;
    }

    @Override
    public ConfirmResult confirm(Map<String, Object> request) {
        String authorization = Base64
                .getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        TossConfirmResponse response = tossPaymentClient.confirmPayment(authorization, request);
        return ConfirmResult.from(response);
    }

    @Override
    public PaymentFailPayload fail() {
        return null;
    }

    @Override
    public PaymentProvider support() {
        return PaymentProvider.TOSS;
    }
}
