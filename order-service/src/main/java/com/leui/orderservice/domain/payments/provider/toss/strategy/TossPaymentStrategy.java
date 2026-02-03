package com.leui.orderservice.domain.payments.provider.toss.strategy;

import com.leui.orderservice.domain.order.dto.OrderCreateRequest;
import com.leui.orderservice.domain.order.entity.Order;
import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.dto.provider.TossConfirmResponse;
import com.leui.orderservice.domain.payments.dto.provider.TossReadyPayload;
import com.leui.orderservice.domain.payments.provider.ConfirmResult;
import com.leui.orderservice.domain.payments.provider.PaymentStrategy;
import com.leui.orderservice.domain.payments.provider.toss.feignclient.TossPaymentClient;
import com.leui.orderservice.global.feignclient.StoreDealFeignClient;
import com.leui.orderservice.global.feignclient.UserFeignClient;
import dto.payment.TossSuccessParam;
import dto.store.DealDetailResponse;
import dto.user.UserDetailResponse;
import enumtype.OrderStatus;
import enumtype.PaymentProvider;
import feign.FeignException;
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

    @Override
    public PaymentReadyResponse ready(PaymentReadyRequest request) {
        UserDetailResponse userDetail = userFeignClient.getUserDetail(request.userId());

        return new TossReadyPayload(
                request.order().getId(),
                baseUrl + "/api/v1/payments/toss/confirm",
                baseUrl + "/api/v1/payments/toss/fail",
                userDetail.eamil(),
                userDetail.eamil());
    }

    @Override
    public ConfirmResult confirmPay(TossSuccessParam param, Order order) {
        String authorization = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        OrderStatus status;
        try {
            tossPaymentClient.confirmPayment(authorization, param);
            status = OrderStatus.PAYMENT_DONE;
        } catch (FeignException e) {
            status = OrderStatus.FAIL_PAYMENT_ABORTED;
        }
        return new ConfirmResult(PaymentProvider.TOSS, status);
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
