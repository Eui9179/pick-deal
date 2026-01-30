package com.leui.orderservice.global.facade;

import com.leui.orderservice.domain.order.dto.OrderCreateRequest;
import com.leui.orderservice.domain.order.entity.Order;
import dto.payment.*;
import enumtype.OrderStatus;
import com.leui.orderservice.domain.order.service.OrderService;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.provider.ConfirmResult;
import com.leui.orderservice.domain.payments.provider.PaymentProviderHandler;
import com.leui.orderservice.global.feignclient.StoreDealFeignClient;
import dto.store.DealStockQuantityRequest;
import enumtype.PaymentProvider;
import exception.OutOfStockException;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderPaymentService {

    private final PaymentProviderHandler paymentProviderHandler;
    private final OrderService orderService;
    private final StoreDealFeignClient storeDealFeignClient;

    @Transactional
    public PaymentReadyResponse startOrderTransaction(OrderCreateRequest request, Long userId) {
        Order order = orderService.createOrder(userId, request);
        decreaseDealStock(order, request.dealId(), request.quantity());
        return paymentProviderHandler.ready(request, order.getId(), userId);
    }

    @Transactional
    public ConfirmResult confirmToss(TossSuccessParam param) {
        return confirmPayment(PaymentProvider.TOSS, param);
    }

    @Transactional
    public ConfirmResult confirmKakao(KakaoSuccessParam param) {
        return confirmPayment(PaymentProvider.KAKAO, param);
    }

    @Transactional
    public PaymentFailResponse failPayment(PaymentFailRequest param) {
        Order order = orderService.getOrder(param.orderId());
        OrderStatus status = OrderStatus.from(param.failCode());
        order.setStatus(status);
        storeDealFeignClient.rollbackStock(new DealStockQuantityRequest(order.getId(), order.getQuantity()));
        return new PaymentFailResponse(status);
    }

    private ConfirmResult confirmPayment(PaymentProvider provider, PaymentSuccessParam param) {
        return paymentProviderHandler.confirm(provider, param);

    }

    private void decreaseDealStock(Order order, Long dealId, int quantity) {
        try {
            storeDealFeignClient.reserveStock(dealId, new DealStockQuantityRequest(order.getId(), quantity));
            order.setStatus(OrderStatus.ORDER_START);
        } catch (FeignException.NotFound e) {
            order.setErrorStatus(OrderStatus.FAIL_DEAL_NOTFOUND, e.getMessage());
            throw new EntityNotFoundException(e.getMessage());
        } catch (FeignException.Conflict e) {
            order.setErrorStatus(OrderStatus.FAIL_OUT_OF_STOCK, e.getMessage());
            throw new OutOfStockException(e.getMessage());
        }
    }

}
