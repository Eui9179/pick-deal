package com.leui.orderservice.domain.payments.provider.toss.strategy;

import com.leui.orderservice.domain.order.entity.Order;
import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.dto.provider.TossConfirmResponse;
import com.leui.orderservice.domain.payments.dto.provider.TossReadyPayload;
import com.leui.orderservice.domain.payments.provider.ConfirmResult;
import com.leui.orderservice.domain.payments.provider.PaymentStrategy;
import com.leui.orderservice.domain.payments.provider.toss.feignclient.TossPaymentClient;
import com.leui.orderservice.global.feignclient.UserFeignClient;
import dto.payment.PaymentSuccessParam;
import dto.payment.TossSuccessParam;
import dto.user.UserDetailResponse;
import enumtype.OrderStatus;
import enumtype.PaymentProvider;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RequiredArgsConstructor
@Component
public class TossPaymentStrategy implements PaymentStrategy {

    @Value("${toss.secret-key}")
    private String secretKey;

    @Value("${gateway.base-url}")
    private String baseUrl;

    private final TossPaymentClient tossPaymentClient;
    private final UserFeignClient userFeignClient;

    @Override
    public PaymentReadyResponse ready(PaymentReadyRequest request) {
        UserDetailResponse userDetail = userFeignClient.getUserDetail(request.userId());

        return new TossReadyPayload(
                request.order().getId(),
                baseUrl + "/api/v1/payments/toss/confirm",
                baseUrl + "/api/v1/payments/toss/fail",
                userDetail.eamil(),
                userDetail.eamil()
        );
    }

    @Override
    public ConfirmResult approve(PaymentSuccessParam param, Order order) {
        if (!(param instanceof TossSuccessParam)) {
            throw new IllegalArgumentException("Invalid parameter type for Toss");
        }

        String authorization = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        try {
            ResponseEntity<TossConfirmResponse> response =
                    tossPaymentClient.confirmPayment(authorization, (TossSuccessParam) param);
            order.setPaymentKey(response.getBody().paymentKey());
            return new ConfirmResult(PaymentProvider.TOSS, OrderStatus.PAYMENT_DONE, "");
        } catch (FeignException e) {
            return new ConfirmResult(PaymentProvider.TOSS, OrderStatus.PAYMENT_FAILED, e.getMessage());
        }
    }

    @Override
    public PaymentProvider support() {
        return PaymentProvider.TOSS;
    }



}
