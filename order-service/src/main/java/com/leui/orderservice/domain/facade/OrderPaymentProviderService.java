package com.leui.orderservice.domain.facade;

import com.leui.orderservice.domain.order.dto.OrderCancelRequest;
import com.leui.orderservice.domain.order.dto.OrderCancelResponse;
import com.leui.orderservice.domain.order.dto.OrderCreateRequest;
import com.leui.orderservice.domain.order.entity.Order;
import com.leui.orderservice.domain.order.service.OrderService;
import com.leui.orderservice.domain.payments.dto.PaymentFailParam;
import com.leui.orderservice.domain.payments.dto.PaymentReadyRequest;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.dto.PaymentStatusResponse;
import com.leui.orderservice.domain.payments.provider.ApproveResult;
import com.leui.orderservice.domain.payments.provider.PaymentProviderHandler;
import com.leui.orderservice.global.exception.ForbiddenException;
import com.leui.orderservice.global.feignclient.StoreDealFeignClient;
import dto.payment.PaymentFailResponse;
import dto.payment.PaymentSuccessParam;
import dto.store.DealDetailResponse;
import dto.store.DealStockQuantityRequest;
import enumtype.OrderStatus;
import enumtype.PaymentProvider;
import kafka.event.PaymentDoneEvent;
import exception.OutOfStockException;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import kafka.topic.KafkaTopics;
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
    private final KafkaTemplate<String, PaymentDoneEvent> kafkaTemplate;


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

        return paymentProviderHandler.ready(PaymentReadyRequest.builder()
                .provider(request.provider())
                .order(order)
                .userId(userId)
                .dealName(dealDetail.name())
                .quantity(request.quantity())
                .totalAmount(totalAmount)
                .build());
    }

    @Transactional
    public ApproveResult approvePayments(PaymentProvider provider, PaymentSuccessParam param) {
        Order order = orderService.getOrder(param.getOrderId());
        ApproveResult result = paymentProviderHandler.approve(provider, param, order);
        order.setStatus(result.status());
        order.setFailDescription(result.failCode());

        if (result.status() == OrderStatus.PAYMENT_DONE) {
            kafkaTemplate.send(
                    KafkaTopics.PAYMENT_DONE,
                    order.getId(),
                    PaymentDoneEvent.builder()
                            .orderId(order.getId())
                            .status(OrderStatus.PAYMENT_DONE)
                            .dealId(order.getDealId())
                            .quantity(order.getQuantity())
                            .build()
            );
        }

        if (result.status() == OrderStatus.PAYMENT_DONE) {
            publishMessageEvent(PaymentDoneEvent.builder()
                    .orderId(order.getId())
                    .status(OrderStatus.PAYMENT_DONE)
                    .dealId(order.getDealId())
                    .quantity(order.getQuantity())
                    .build());
        } else {
            publishMessageEvent(PaymentDoneEvent.builder()
                    .orderId(order.getId())
                    .status(OrderStatus.PAYMENT_FAILED)
                    .dealId(order.getDealId())
                    .quantity(order.getQuantity())
                    .build());
        }

        return result;
    }

    @Transactional
    public PaymentFailResponse failPayment(PaymentFailParam param) {
        Order order = orderService.getOrder(param.orderId());
        OrderStatus status = OrderStatus.PAYMENT_FAILED;
        order.setStatus(status);
        order.setFailDescription(param.failCode());
        publishMessageEvent(PaymentDoneEvent.builder()
                .orderId(order.getId())
                .status(status)
                .dealId(order.getDealId())
                .quantity(order.getQuantity())
                .failDescription(param.failCode())
                .build());
        return new PaymentFailResponse(order.getId(), OrderStatus.PAYMENT_FAILED);
    }

    public PaymentStatusResponse status(String orderId) {
        Order order = orderService.getOrder(orderId);
        return new PaymentStatusResponse(order.getId(), order.getStatus(), order.getFailDescription());
    }

    @Transactional
    public OrderCancelResponse cancel(String orderId, Long userId, OrderCancelRequest request) {
        // TODO 결제 취소 이벤트 발행
        Order order = orderService.getOrder(orderId);
        if (!order.getUserId().equals(userId)) {
            throw new ForbiddenException("Forbidden userId = " + userId);
        }

        OrderCancelResponse cancel = paymentProviderHandler.cancel(order, request);
        order.setStatus(cancel.orderStatus());
        return cancel;
    }

    private void publishMessageEvent(PaymentDoneEvent event) {
        kafkaTemplate.send(PaymentDoneEvent.TOPIC, event.getOrderId(), event);
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
