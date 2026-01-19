package com.leui.orderservice.domain.payments.provider.toss.strategy;

import com.leui.orderservice.domain.payments.dto.PaymentFailPayload;
import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.dto.provider.TossConfirmResponse;
import com.leui.orderservice.domain.payments.dto.provider.TossReadyPayload;
import enumtype.PaymentProvider;
import com.leui.orderservice.domain.payments.provider.ConfirmResult;
import com.leui.orderservice.domain.payments.provider.PaymentStrategy;
import com.leui.orderservice.domain.payments.provider.toss.feignclient.TossPaymentClient;
import com.leui.orderservice.global.feignclient.StoreDealFeignClient;
import com.leui.orderservice.global.feignclient.UserFeignClient;
import dto.payment.TossSuccessParam;
import dto.store.DealDetailResponse;
import dto.user.UserDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    @Override
    public PaymentReadyResponse ready(PaymentReadyRequest request, Long userId) {
        DealDetailResponse dealDetail = feignClient.getDealDetail(request.dealId());
        if (!request.amount().equals(dealDetail.discountPrice())) {
            throw new RuntimeException("Mount is not equal. " +
                    "Input amount = " + request.amount() +
                    "Actual amount = " + dealDetail.discountPrice());
        }
        UserDetailResponse userDetail = userFeignClient.getUserDetail(userId);

        return new TossReadyPayload(request.orderId(),
                baseUrl + "/api/v1/payments/toss/confirm",
                baseUrl + "/api/v1/payments/fail",
                userDetail.eamil(),
                userDetail.eamil());
    }

    @Override
    public ConfirmResult confirm(TossSuccessParam param) {
        String authorization = Base64
                .getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        TossConfirmResponse response = tossPaymentClient.confirmPayment(authorization, param);
        return ConfirmResult.from(support(), response.status());
    }

    @Override
    public PaymentFailPayload fail() {
        return null;
    }

    @Override
    public PaymentProvider support() {
        return PaymentProvider.TOSS;
    }

    @Override
    public Class<TossSuccessParam> paramType() {
        return TossSuccessParam.class;
    }

}
