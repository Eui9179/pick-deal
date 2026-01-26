package com.leui.orderservice.domain.payments.provider.kakao.strategy;

import com.leui.orderservice.domain.order.dto.OrderCreateRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.dto.provider.KakaoConfirmRequest;
import com.leui.orderservice.domain.payments.dto.provider.KakaoConfirmResponse;
import com.leui.orderservice.domain.payments.dto.provider.KakaoReadyPayload;
import com.leui.orderservice.domain.payments.dto.provider.KakaoReadyRequest;
import com.leui.orderservice.domain.payments.provider.ConfirmResult;
import com.leui.orderservice.domain.payments.provider.PaymentStrategy;
import com.leui.orderservice.domain.payments.provider.kakao.feignclient.KakaoPaymentClient;
import com.leui.orderservice.global.feignclient.StoreDealFeignClient;
import dto.payment.KakaoSuccessParam;
import dto.store.DealDetailResponse;
import enumtype.OrderStatus;
import enumtype.PaymentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

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

    private final String SECRET_KEY = "SECRET_KEY ";

    private final KakaoPaymentClient kakaoPaymentClient;
    private final StoreDealFeignClient storeDealFeignClient;

    @Override
    public PaymentReadyResponse ready(OrderCreateRequest request, String orderId, Long userId) {
        DealDetailResponse dealDetail = storeDealFeignClient.getDealDetail(request.dealId());
        BigDecimal totalAmount = dealDetail.discountPrice().multiply(BigDecimal.valueOf(request.quantity()));
        KakaoReadyRequest readyRequest = new KakaoReadyRequest(
                cid,
                orderId,
                String.valueOf(userId),
                dealDetail.name(),
                request.quantity(),
                totalAmount,
                successUrl,
                cancelUrl,
                failUrl
        );
        KakaoReadyPayload payload = kakaoPaymentClient.ready(SECRET_KEY + adminKey, readyRequest);
        // TODO payload.tid() 저장
        return payload;
    }

    @Override
    public ConfirmResult confirmPay(KakaoSuccessParam param) {
        // TODO payload.tid() 가져오기
        String tid = "";
        KakaoConfirmRequest request = new KakaoConfirmRequest(cid, tid, param);
        KakaoConfirmResponse confirm = kakaoPaymentClient.confirm(SECRET_KEY + adminKey, request);
        // TODO DB에 결과 저장
        return new ConfirmResult(OrderStatus.PAYMENT_DONE);
    }

    @Override
    public PaymentProvider support() {
        return null;
    }

    @Override
    public Class<KakaoSuccessParam> type() {
        return null;
    }
}
