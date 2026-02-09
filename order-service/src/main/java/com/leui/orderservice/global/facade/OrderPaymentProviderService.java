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
import dto.store.NotiReadyPickup;
import enumtype.OrderStatus;
import enumtype.PaymentProvider;
import event.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class OrderPaymentProviderService {

    private final PaymentProviderHandler paymentProviderHandler;
    private final OrderService orderService;
    private final StoreDealFeignClient storeDealFeignClient;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;


    @Transactional
    public PaymentReadyResponse startOrderTransaction(OrderCreateRequest request, Long userId) {
        DealDetailResponse dealDetail = storeDealFeignClient.getDealDetail(request.dealId());
        BigDecimal totalAmount = dealDetail.discountPrice().multiply(BigDecimal.valueOf(request.quantity()));

        if (!request.amount().equals(totalAmount)) {
            throw new IllegalArgumentException("Amount mismatch. expected=" + totalAmount +
                    ", actual=" + request.amount());
        }

        Order order = orderService.createOrder(userId, request, totalAmount);

        publishMessageEvent(OrderEvent.builder()
                .orderId(order.getId())
                .status(OrderStatus.ORDER_START)
                .dealId(request.dealId())
                .quantity(request.quantity())
                .userId(userId)
                .build());

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
        publishMessageEvent(OrderEvent.builder()
                .orderId(order.getId())
                .status(OrderStatus.FAIL_PAYMENT_CANCELED)
                .dealId(order.getDealId())
                .quantity(order.getQuantity())
                .failDescription(param.failCode())
                .build());
        return new PaymentFailResponse(status);
    }

    private ConfirmResult confirmPayment(PaymentProvider provider, PaymentSuccessParam param) {
        Order order = orderService.getOrder(param.getOrderId());
        ConfirmResult result = paymentProviderHandler.confirm(provider, param, order);
        order.setStatus(result.getStatus());
        if (result.getStatus() == OrderStatus.PAYMENT_DONE) {
            publishMessageEvent(OrderEvent.builder()
                    .orderId(order.getId())
                    .status(OrderStatus.PAYMENT_DONE)
                    .dealId(order.getDealId())
                    .quantity(order.getQuantity())
                    .build());
//            storeDealFeignClient.commitStock(order.getDealId(), new DealStockQuantityRequest(order.getId(), order.getQuantity()));
//            storeDealFeignClient.notiReadyPickup(new NotiReadyPickup());
        } else {
            publishMessageEvent(OrderEvent.builder()
                    .orderId(order.getId())
                    .status(OrderStatus.FAIL_PAYMENT_CANCELED)
                    .dealId(order.getDealId())
                    .quantity(order.getQuantity())
                    .build());
//            storeDealFeignClient.rollbackStock(new DealStockQuantityRequest(order.getId(), order.getQuantity()));
        }

        return result;
    }

    private void publishMessageEvent(OrderEvent event) {
        kafkaTemplate.send(OrderEvent.TOPIC, event.getEventId(), event);
    }

}
