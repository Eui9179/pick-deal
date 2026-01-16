package com.leui.orderservice.domain.payments.strategy.toss;

import com.leui.orderservice.domain.payments.dto.PaymentFailPayload;
import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.entity.PaymentProvider;
import com.leui.orderservice.domain.payments.feignclient.TossPaymentClient;
import com.leui.orderservice.domain.payments.strategy.ConfirmResult;
import com.leui.orderservice.domain.payments.strategy.PaymentStrategy;
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

    @Override
    public PaymentReadyResponse ready(PaymentReadyRequest request) {
        // 주문 가격 검증
        // 상품 이름
        // 주문자 이름
        return null;
    }

    @Override
    public ConfirmResult confirm(Map<String, Object> request) {
        String authorization = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
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
