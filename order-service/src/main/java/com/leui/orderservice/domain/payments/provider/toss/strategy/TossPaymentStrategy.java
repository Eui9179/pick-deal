package com.leui.orderservice.domain.payments.provider.toss.strategy;

import com.leui.orderservice.domain.order.dto.OrderCancelRequest;
import com.leui.orderservice.domain.order.dto.OrderCancelResponse;
import com.leui.orderservice.domain.order.entity.Order;
import com.leui.orderservice.domain.payments.dto.ApproveResult;
import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.dto.provider.TossApproveResponse;
import com.leui.orderservice.domain.payments.dto.provider.TossCancelParam;
import com.leui.orderservice.domain.payments.dto.provider.TossReadyPayload;
import com.leui.orderservice.domain.payments.dto.provider.TossSuccessParam;
import com.leui.orderservice.domain.payments.provider.PaymentStrategy;
import com.leui.orderservice.domain.payments.provider.toss.feignclient.TossPaymentClient;
import com.leui.orderservice.global.feignclient.UserFeignClient;
import dto.payment.PaymentSuccessParam;
import dto.user.UserDetailResponse;
import enumtype.OrderStatus;
import enumtype.PaymentProvider;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Override
    public ApproveResult approve(PaymentSuccessParam param, Order order) {
        if (!(param instanceof TossSuccessParam)) {
            throw new IllegalArgumentException("Invalid parameter type for Toss");
        }
        try {
            ResponseEntity<TossApproveResponse> response =
                    tossPaymentClient.approve(generateAuthorization(), (TossSuccessParam) param);
            order.setPaymentKey(response.getBody().paymentKey());
            return new ApproveResult(PaymentProvider.TOSS, OrderStatus.PAYMENT_APPROVE, "");
        } catch (FeignException e) {
            return new ApproveResult(PaymentProvider.TOSS, OrderStatus.PAYMENT_FAILED, e.getMessage());
        }
    }

    @Override
    public PaymentProvider support() {
        return PaymentProvider.TOSS;
    }

    @Override
    public OrderCancelResponse cancel(Order order, OrderCancelRequest request) {
        tossPaymentClient.cancel(
                generateAuthorization(),
                order.getPaymentKey(),
                new TossCancelParam(request.cancelReason(), order.getTotalAmount().toString())
        );
        return new OrderCancelResponse(OrderStatus.ORDER_CANCELED);
    }

    private String generateAuthorization() {
        return Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }

}
