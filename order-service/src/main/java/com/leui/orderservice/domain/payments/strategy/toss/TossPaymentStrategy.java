package com.leui.orderservice.domain.payments.strategy.toss;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leui.orderservice.domain.payments.dto.*;
import com.leui.orderservice.domain.payments.entity.PaymentProvider;
import com.leui.orderservice.domain.payments.strategy.ConfirmResult;
import com.leui.orderservice.domain.payments.strategy.PaymentStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public class TossPaymentStrategy implements PaymentStrategy {

    @Value("${toss.secret-key}")
    private String secretKey;

    @Value("${toss.url}")
    private String tossUrl;

    @Override
    public PaymentReadyResponse ready(PaymentReadyRequest request) {
        // 주문 가격 검증
        // 상품 이름
        // 주문자 이름
        return null;
    }

    @Override
    public ConfirmResult confirm(PaymentConfirmRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String base64Auth = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + base64Auth);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "paymentKey", request.paymentKey(),
                "orderId", request.orderId(),
                "amount", request.amount()
        );
        HttpEntity<Map<String, Object>> httpRequest = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(tossUrl, httpRequest, String.class);
            TossConfirmResponse tossConfirmResponse = new ObjectMapper().readValue(response.getBody(), TossConfirmResponse.class);
            return ConfirmResult.from(tossConfirmResponse);
        } catch (Exception e) {
            throw new RuntimeException("토스 결제 승인 실패");
        }
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
