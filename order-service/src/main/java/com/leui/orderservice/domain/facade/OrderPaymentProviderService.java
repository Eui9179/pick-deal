package com.leui.orderservice.domain.facade;

import com.leui.orderservice.domain.order.dto.OrderCancelRequest;
import com.leui.orderservice.domain.order.dto.OrderCancelResponse;
import com.leui.orderservice.domain.order.dto.OrderCreateRequest;
import com.leui.orderservice.domain.order.entity.Order;
import com.leui.orderservice.domain.order.service.OrderService;
import com.leui.orderservice.domain.payments.dto.*;
import com.leui.orderservice.domain.payments.provider.PaymentProviderHandler;
import com.leui.orderservice.global.exception.ForbiddenException;
import com.leui.orderservice.global.feignclient.StoreDealFeignClient;
import com.leui.orderservice.global.feignclient.UserFeignClient;
import dto.payment.PaymentFailResponse;
import dto.payment.PaymentSuccessParam;
import dto.store.DealDetailResponse;
import dto.store.DealStockQuantityRequest;
import dto.user.UserPointResponse;
import enumtype.OrderStatus;
import enumtype.PaymentProvider;
import exception.OutOfStockException;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import kafka.event.PaymentCancelEvent;
import kafka.event.PaymentApproveEvent;
import kafka.event.PaymentFailEvent;
import kafka.topic.EventTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class OrderPaymentProviderService {

    private final PaymentProviderHandler paymentProviderHandler;
    private final OrderService orderService;
    private final StoreDealFeignClient storeDealFeignClient;
    private final UserFeignClient userFeignClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public PaymentReadyResponse startOrderTransaction(OrderCreateRequest request, Long userId) {
        DealDetailResponse dealDetail = storeDealFeignClient.getDealDetail(request.dealId());
        UserPointResponse userPoint = userFeignClient.getUserPoint(userId);

        // 사용할 포인트가 보유한 포인트보다 많은 경우
        if (userPoint.point().compareTo(request.usedPoint()) < 0) {
            throw new IllegalArgumentException("User Point mismatch. User's Point : " + userPoint +
                    ", Request Point :" + request.usedPoint());
        }

        BigDecimal totalAmount = dealDetail.discountPrice()
                .multiply(BigDecimal.valueOf(request.quantity()))
                .subtract(request.usedPoint());

        if (!request.totalAmount().equals(totalAmount)) {
            throw new IllegalArgumentException("Amount mismatch. expected=" + totalAmount +
                    ", actual=" + request.totalAmount());
        }

        Order order = orderService.createOrder(userId, request, totalAmount);
        decreaseDealStock(order, request.dealId(), request.quantity()); // 임시 재고 감소
        order.updateOrderCreated();

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
    public PaymentTrackingResponse approvePayments(PaymentProvider provider, PaymentSuccessParam param) {
        Order order = orderService.getOrder(param.getOrderId());
        approvePayment(provider, param, order);
        return new PaymentTrackingResponse(order.getId());
    }

    @Async
    public void approvePayment(PaymentProvider provider, PaymentSuccessParam param, Order order) {
        ApproveResult result = paymentProviderHandler.approve(provider, param, order);
        if (result.status() == OrderStatus.PAYMENT_APPROVE) {
            order.updatePaymentDone();
            kafkaTemplate.send(EventTopics.PAYMENT_APPROVE, order.getId(),
                    PaymentApproveEvent.builder()
                            .orderId(order.getId())
                            .dealId(order.getDealId())
                            .userId(order.getUserId())
                            .quantity(order.getQuantity())
                            .totalAmount(order.getTotalAmount())
                            .usedPoint(order.getUsedPoint())
                            .build());
        } else {
            order.updatePaymentFail();
            order.setFailDescription(result.failCode());
            kafkaTemplate.send(EventTopics.PAYMENT_APPROVE_FAIL, order.getId(),
                    PaymentFailEvent.builder()
                            .orderId(order.getId())
                            .dealId(order.getDealId())
                            .totalAmount(order.getTotalAmount())
                            .quantity(order.getQuantity())
                            .userId(order.getUserId())
                            .paymentKey(order.getPaymentKey())
                            .failDescription(result.failCode())
                            .build());
        }
    }

    @Transactional
    public PaymentFailResponse failPayment(PaymentFailParam param) {
        Order order = orderService.getOrder(param.orderId());
        order.updatePaymentFail();
        order.setFailDescription(param.failCode());
        kafkaTemplate.send(EventTopics.PAYMENT_APPROVE_FAIL, order.getId(),
                PaymentFailEvent.builder()
                        .orderId(order.getId())
                        .dealId(order.getDealId())
                        .totalAmount(order.getTotalAmount())
                        .quantity(order.getQuantity())
                        .userId(order.getUserId())
                        .paymentKey(order.getPaymentKey())
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
        Order order = orderService.getOrder(orderId);
        if (!order.getUserId().equals(userId)) {
            throw new ForbiddenException("Forbidden userId = " + userId);
        }

        OrderCancelResponse cancel = paymentProviderHandler.cancel(order, request);
        order.updatePaymentCancel();

        kafkaTemplate.send(EventTopics.PAYMENT_CANCELED, order.getId(),
                PaymentCancelEvent.builder()
                        .orderId(order.getId())
                        .dealId(order.getDealId())
                        .totalAmount(order.getTotalAmount())
                        .quantity(order.getQuantity())
                        .userId(order.getUserId())
                        .paymentKey(order.getPaymentKey())
                        .build());

        return cancel;
    }

    private void decreaseDealStock(Order order, Long dealId, int quantity) {
        try {
            storeDealFeignClient.reserveStock(dealId, new DealStockQuantityRequest(order.getId(), quantity));
        } catch (FeignException.NotFound e) {
            order.setErrorStatus(OrderStatus.FAIL_DEAL_NOTFOUND, e.getMessage());
            throw new EntityNotFoundException(e.getMessage());
        } catch (FeignException.Conflict e) {
            order.setErrorStatus(OrderStatus.FAIL_OUT_OF_STOCK, e.getMessage());
            throw new OutOfStockException(e.getMessage());
        }
    }

}
