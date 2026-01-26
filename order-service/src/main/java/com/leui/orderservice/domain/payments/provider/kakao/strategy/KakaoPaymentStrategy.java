package com.leui.orderservice.domain.payments.provider.kakao.strategy;

import com.leui.orderservice.domain.order.dto.OrderCreateRequest;
import com.leui.orderservice.domain.order.entity.Order;
import com.leui.orderservice.domain.order.repository.OrderRepository;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.dto.provider.KakaoConfirmRequest;
import com.leui.orderservice.domain.payments.dto.provider.KakaoConfirmResponse;
import com.leui.orderservice.domain.payments.dto.provider.KakaoReadyPayload;
import com.leui.orderservice.domain.payments.dto.provider.KakaoReadyRequest;
import com.leui.orderservice.domain.payments.entity.Payment;
import com.leui.orderservice.domain.payments.provider.ConfirmResult;
import com.leui.orderservice.domain.payments.provider.PaymentStrategy;
import com.leui.orderservice.domain.payments.provider.kakao.feignclient.KakaoPaymentClient;
import com.leui.orderservice.domain.payments.repository.PaymentRepository;
import com.leui.orderservice.global.feignclient.StoreDealFeignClient;
import dto.payment.KakaoSuccessParam;
import dto.store.DealDetailResponse;
import enumtype.OrderStatus;
import enumtype.PaymentProvider;
import jakarta.persistence.EntityNotFoundException;
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

    private static final String AUTHORIZATION_PREFIX = "SECRET_KEY ";

    private final KakaoPaymentClient kakaoPaymentClient;
    private final StoreDealFeignClient storeDealFeignClient;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public PaymentReadyResponse ready(OrderCreateRequest request, String orderId, Long userId) {
        // 1. Deal 정보 조회 및 금액 검증
        DealDetailResponse dealDetail = storeDealFeignClient.getDealDetail(request.dealId());
        BigDecimal actualAmount = dealDetail.discountPrice().multiply(BigDecimal.valueOf(request.quantity()));
        if (!request.amount().equals(actualAmount)) {
            throw new IllegalArgumentException("Amount mismatch. expected=" + actualAmount + ", actual=" + request.amount());
        }

        // 2. Kakao 결제 준비 요청
        KakaoReadyRequest readyRequest = new KakaoReadyRequest(
                cid,
                orderId,
                String.valueOf(userId),
                dealDetail.name(),
                request.quantity(),
                actualAmount,
                successUrl,
                cancelUrl,
                failUrl
        );
        KakaoReadyPayload payload = kakaoPaymentClient.ready(AUTHORIZATION_PREFIX + adminKey, readyRequest);

        // 3. Payment 엔티티 생성 및 tid 저장
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found. id=" + orderId));
        Payment payment = new Payment(order, PaymentProvider.KAKAO);
        payment.updatePaymentKey(payload.tid());
        paymentRepository.save(payment);

        return payload;
    }

    @Override
    public ConfirmResult confirmPay(KakaoSuccessParam param) {
        // 1. Payment에서 tid 조회
        Payment payment = paymentRepository.findById(param.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Payment not found. orderId=" + param.orderId()));
        String tid = payment.getPaymentKey();

        // 2. Kakao 결제 승인 요청
        KakaoConfirmRequest request = new KakaoConfirmRequest(cid, tid, param);
        KakaoConfirmResponse response = kakaoPaymentClient.confirm(AUTHORIZATION_PREFIX + adminKey, request);

        // 3. Payment 완료 처리
        payment.completePayment(
                BigDecimal.valueOf(response.amount().total()),
                "KAKAO_PAY"
        );
        paymentRepository.save(payment);

        return new ConfirmResult(OrderStatus.PAYMENT_DONE);
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
