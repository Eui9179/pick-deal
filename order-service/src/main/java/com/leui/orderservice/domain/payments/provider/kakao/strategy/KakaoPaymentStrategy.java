package com.leui.orderservice.domain.payments.provider.kakao.strategy;

import com.leui.orderservice.domain.order.entity.Order;
import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.dto.provider.KakaoConfirmRequest;
import com.leui.orderservice.domain.payments.dto.provider.KakaoConfirmResponse;
import com.leui.orderservice.domain.payments.dto.provider.KakaoReadyPayload;
import com.leui.orderservice.domain.payments.dto.provider.KakaoReadyRequest;
import com.leui.orderservice.domain.payments.provider.ConfirmResult;
import com.leui.orderservice.domain.payments.provider.PaymentStrategy;
import com.leui.orderservice.domain.payments.provider.kakao.feignclient.KakaoPaymentClient;
import dto.payment.KakaoSuccessParam;
import enumtype.OrderStatus;
import enumtype.PaymentProvider;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KakaoPaymentStrategy implements PaymentStrategy<KakaoSuccessParam> {

    @Value("${kakao.cid}")
    private String cid;

    @Value("${kakao.admin-key}")
    private String adminKey;

    @Value("${kakao.sucess-url}")
    private String successUrl;

    @Value("${kakao.cancel-url}")
    private String cancelUrl;

    @Value("${kakao.fail-url}")
    private String failUrl;

    private static final String AUTHORIZATION_PREFIX = "SECRET_KEY ";

    private final KakaoPaymentClient kakaoPaymentClient;

    @Override
    public PaymentReadyResponse ready(PaymentReadyRequest request) {
        KakaoReadyRequest readyRequest = new KakaoReadyRequest(
                cid,
                request.order().getId(),
                String.valueOf(request.userId()),
                request.dealName(),
                request.quantity(),
                request.totalAmount(),
                successUrl,
                cancelUrl,
                failUrl
        );

        KakaoReadyPayload payload = kakaoPaymentClient.ready(AUTHORIZATION_PREFIX + adminKey, readyRequest);
        request.order().setPaymentKey(payload.getTid());
        return payload;
    }

    @Override
    public ConfirmResult confirmPay(KakaoSuccessParam param, Order order) {
        KakaoConfirmRequest request = new KakaoConfirmRequest(cid, order.getPaymentKey(), param);
        OrderStatus status;
        try {
            kakaoPaymentClient.confirm(AUTHORIZATION_PREFIX + adminKey, request);
            status = OrderStatus.PAYMENT_DONE;
        } catch (FeignException e) {
            status = OrderStatus.FAIL_PAYMENT_ABORTED;
        }
        return new ConfirmResult(PaymentProvider.KAKAO, status);

    }

    @Override
    public PaymentProvider support() {
        return PaymentProvider.KAKAO;
    }

    @Override
    public Class<KakaoSuccessParam> type() {
        return KakaoSuccessParam.class;
    }

}
