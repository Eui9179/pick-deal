package com.leui.orderservice.global.facade;

import com.leui.orderservice.domain.order.dto.OrderCreateRequest;
import com.leui.orderservice.domain.order.entity.Order;
import com.leui.orderservice.domain.order.service.OrderService;
import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.provider.ConfirmResult;
import com.leui.orderservice.domain.payments.provider.PaymentProviderHandler;
import com.leui.orderservice.global.feignclient.StoreDealFeignClient;
import dto.payment.*;
import dto.store.DealDetailResponse;
import dto.store.DealStockQuantityRequest;
import enumtype.OrderStatus;
import enumtype.PaymentProvider;
import exception.OutOfStockException;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class OrderPaymentProviderService {

    private final PaymentProviderHandler paymentProviderHandler;
    private final OrderService orderService;
    private final StoreDealFeignClient storeDealFeignClient;

    @Transactional
    public PaymentReadyResponse startOrderTransaction(OrderCreateRequest request, Long userId) {
        DealDetailResponse dealDetail = storeDealFeignClient.getDealDetail(request.dealId());
        BigDecimal totalAmount = dealDetail.discountPrice().multiply(BigDecimal.valueOf(request.quantity()));

        if (!request.amount().equals(totalAmount)) {
            throw new IllegalArgumentException("Amount mismatch. expected=" + totalAmount +
                    ", actual=" + request.amount());
        }

        Order order = orderService.createOrder(userId, request, totalAmount);
        decreaseDealStock(order, request.dealId(), request.quantity());

        PaymentReadyRequest readyRequest = new PaymentReadyRequest(
                request.provider(), order, userId, dealDetail.name(), request.quantity(), totalAmount
        );

        return paymentProviderHandler.ready(readyRequest);
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
        OrderStatus status = OrderStatus.FAIL_PAYMENT_CANCELED;
        order.setStatus(status);
        // TODO 이벤트 처리
        storeDealFeignClient.rollbackStock(new DealStockQuantityRequest(order.getId(), order.getQuantity()));
        return new PaymentFailResponse(status);
    }

    private ConfirmResult confirmPayment(PaymentProvider provider, PaymentSuccessParam param) {
        Order order = orderService.getOrder(param.getOrderId());
        ConfirmResult result = paymentProviderHandler.confirm(provider, param, order);
        order.setStatus(result.getStatus());
        // TODO 이벤트 처리
        if (result.getStatus() == OrderStatus.PAYMENT_DONE) {
            // TODO 사장님 알림
            storeDealFeignClient.commitStock(order.getDealId(), new DealStockQuantityRequest(order.getId(), order.getQuantity()));
        } else {
            storeDealFeignClient.rollbackStock(new DealStockQuantityRequest(order.getId(), order.getQuantity()));
        }

        return result;
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
