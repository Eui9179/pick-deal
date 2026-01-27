package com.leui.orderservice.domain.payments.provider.toss.strategy;

import com.leui.orderservice.domain.order.dto.OrderCreateRequest;
import com.leui.orderservice.domain.order.entity.Order;
import com.leui.orderservice.domain.order.repository.OrderRepository;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.dto.provider.TossConfirmResponse;
import com.leui.orderservice.domain.payments.dto.provider.TossReadyPayload;
import com.leui.orderservice.domain.payments.entity.Payment;
import com.leui.orderservice.domain.payments.provider.ConfirmResult;
import com.leui.orderservice.domain.payments.provider.PaymentStrategy;
import com.leui.orderservice.domain.payments.provider.toss.feignclient.TossPaymentClient;
import com.leui.orderservice.domain.payments.repository.PaymentRepository;
import com.leui.orderservice.global.feignclient.StoreDealFeignClient;
import com.leui.orderservice.global.feignclient.UserFeignClient;
import dto.payment.TossSuccessParam;
import dto.store.DealDetailResponse;
import dto.user.UserDetailResponse;
import enumtype.PaymentProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RequiredArgsConstructor
@Component
public class TossPaymentStrategy implements PaymentStrategy<TossSuccessParam> {

    @Value("${toss.secret-key}")
    private String secretKey;

    @Value("${gateway.base-url}")
    private String baseUrl;

    private final TossPaymentClient tossPaymentClient;
    private final UserFeignClient userFeignClient;
    private final StoreDealFeignClient feignClient;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public PaymentReadyResponse ready(OrderCreateRequest request, String orderId, Long userId) {
        // 1. Deal 정보 조회 및 금액 검증
        DealDetailResponse dealDetail = feignClient.getDealDetail(request.dealId());
        BigDecimal actualAmount = dealDetail.discountPrice().multiply(BigDecimal.valueOf(request.quantity()));
        if (!request.amount().equals(actualAmount)) {
            throw new IllegalArgumentException("Amount mismatch. expected=" + actualAmount + ", actual=" + request.amount());
        }

        // 2. User 정보 조회
        UserDetailResponse userDetail = userFeignClient.getUserDetail(userId);

        // 3. Payment 엔티티 생성 (Toss는 ready 시점에 paymentKey 없음)
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found. id=" + orderId));
        Payment payment = new Payment(order, PaymentProvider.TOSS);
        paymentRepository.save(payment);

        return new TossReadyPayload(
                orderId,
                baseUrl + "/api/v1/payments/toss/confirm",
                baseUrl + "/api/v1/payments/toss/fail",
                userDetail.eamil(),
                userDetail.eamil());
    }

    @Override
    public ConfirmResult confirmPay(TossSuccessParam param) {
        // 1. Toss 결제 승인 요청
        String authorization = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        TossConfirmResponse response = tossPaymentClient.confirmPayment(authorization, param);

        // 2. Payment에 paymentKey 저장 및 완료 처리
        Payment payment = paymentRepository.findById(param.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Payment not found. orderId=" + param.getOrderId()));
        payment.updatePaymentKey(response.paymentKey());
        payment.completePayment(response.totalAmount(), response.method());
        paymentRepository.save(payment);

        return new ConfirmResult(response.status());
    }

    @Override
    public PaymentProvider support() {
        return PaymentProvider.TOSS;
    }

    @Override
    public Class<TossSuccessParam> type() {
        return TossSuccessParam.class;
    }
}
