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
import exception.OutOfStockException;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import kafka.event.PaymentCancelEvent;
import kafka.event.PaymentDoneEvent;
import kafka.event.PaymentFailEvent;
import kafka.topic.EventTopics;
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
    private final KafkaTemplate<String, Object> kafkaTemplate;

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
            kafkaTemplate.send(EventTopics.PAYMENT_DONE, order.getId(),
                    PaymentDoneEvent.builder()
                            .orderId(order.getId())
                            .status(OrderStatus.PAYMENT_DONE)
                            .dealId(order.getDealId())
                            .quantity(order.getQuantity())
                            .build());
        } else {
            kafkaTemplate.send(EventTopics.PAYMENT_FAIL, order.getId(),
                    PaymentFailEvent.builder()
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
        kafkaTemplate.send(EventTopics.PAYMENT_FAIL, order.getId(),
                PaymentFailEvent.builder().build());
        return new PaymentFailResponse(order.getId(), OrderStatus.PAYMENT_FAILED);
    }

    public PaymentStatusResponse status(String orderId) {
        Order order = orderService.getOrder(orderId);
        return new PaymentStatusResponse(order.getId(), order.getStatus(), order.getFailDescription());
    }

    @Transactional
    public OrderCancelResponse cancel(String orderId, Long userId, OrderCancelRequest request) {
        Order order = orderService.getOrder(orderId);
        if (!order.getUserId().equals(userId)) {
            throw new ForbiddenException("Forbidden userId = " + userId);
        }

        OrderCancelResponse cancel = paymentProviderHandler.cancel(order, request);
        order.setStatus(cancel.orderStatus());

        kafkaTemplate.send(EventTopics.PAYMENT_CANCEL, order.getId(),
                PaymentCancelEvent.builder()
                        .build());

        return cancel;
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
