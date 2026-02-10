package com.leui.orderservice.domain.payments.provider.kakao.strategy;

import com.leui.orderservice.domain.order.dto.OrderCancelRequest;
import com.leui.orderservice.domain.order.dto.OrderCancelResponse;
import com.leui.orderservice.domain.order.entity.Order;
import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.dto.provider.*;
import com.leui.orderservice.domain.payments.provider.ApproveResult;
import com.leui.orderservice.domain.payments.provider.PaymentStrategy;
import com.leui.orderservice.domain.payments.provider.kakao.feignclient.KakaoPaymentClient;
import dto.payment.PaymentSuccessParam;
import enumtype.OrderStatus;
import enumtype.PaymentProvider;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KakaoPaymentStrategy implements PaymentStrategy {

    @Value("${kakao.cid}")
    private String cid;

    @Value("${kakao.admin-key}")
    private String adminKey;

    @Value("${kakao.success-url}")
    private String successUrl;

    @Value("${kakao.cancel-url}")
    private String cancelUrl;

    @Value("${kakao.fail-url}")
    private String failUrl;

    private static final String AUTHORIZATION_PREFIX = "SECRET_KEY ";

    private final KakaoPaymentClient kakaoPaymentClient;

    @Override
    public PaymentReadyResponse ready(PaymentReadyRequest request) {
        Order order = request.order();
        KakaoReadyPayload payload = kakaoPaymentClient.ready(
                AUTHORIZATION_PREFIX + adminKey,
                new KakaoReadyRequest(
                        cid,
                        order.getId(),
                        String.valueOf(request.userId()),
                        request.dealName(),
                        request.quantity(),
                        request.totalAmount(),
                        successUrl,
                        cancelUrl,
                        failUrl
                ));
        order.setPaymentKey(payload.getTid());
        return payload;
    }

    @Override
    public ApproveResult approve(PaymentSuccessParam param, Order order) {
        if (!(param instanceof KakaoSuccessParam)) {
            throw new IllegalArgumentException("Invalid parameter type for Kakao");
        }
        KakaoApproveRequest request =
                new KakaoApproveRequest(cid, order.getPaymentKey(), (KakaoSuccessParam) param);
        try {
            kakaoPaymentClient.approve(AUTHORIZATION_PREFIX + adminKey, request);
            return new ApproveResult(PaymentProvider.KAKAO, OrderStatus.PAYMENT_DONE, "");
        } catch (FeignException e) {
            return new ApproveResult(PaymentProvider.KAKAO, OrderStatus.PAYMENT_FAILED, e.getMessage());
        }
    }

    @Override
    public PaymentProvider support() {
        return PaymentProvider.KAKAO;
    }

    @Override
    public OrderCancelResponse cancel(Order order, OrderCancelRequest request) {
        kakaoPaymentClient.cancel(
                AUTHORIZATION_PREFIX + adminKey,
                new KakaoCancelRequest(cid, order.getPaymentKey(), order.getTotalAmount().intValue(), 0)
        );
        return new OrderCancelResponse(OrderStatus.ORDER_CANCELED);
    }

}
