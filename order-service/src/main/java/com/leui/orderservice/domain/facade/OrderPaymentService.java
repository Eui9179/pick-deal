package com.leui.orderservice.domain.facade;

import com.leui.orderservice.domain.order.dto.OrderCreateRequest;
import com.leui.orderservice.domain.order.entity.Order;
import com.leui.orderservice.domain.order.entity.OrderStatus;
import com.leui.orderservice.domain.order.service.OrderService;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.provider.ConfirmResult;
import com.leui.orderservice.domain.payments.provider.PaymentProviderHandler;
import com.leui.orderservice.global.exception.OrderCreateException;
import com.leui.orderservice.global.feignclient.StoreDealFeignClient;
import dto.store.DealStockDecreaseRequest;
import enumtype.PaymentProvider;
import exception.OutOfStock;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class OrderPaymentService {

    private final PaymentProviderHandler paymentProviderHandler;
    private final OrderService orderService;
    private final StoreDealFeignClient storeDealFeignClient;

    @Transactional
    public PaymentReadyResponse startOrderTransaction(OrderCreateRequest request, Long userId) {
        Order order = orderService.createOrder(userId, request);
        try {
            storeDealFeignClient.decreaseDealStock(request.dealId(), new DealStockDecreaseRequest(request.quantity()));
            order.setStatus(OrderStatus.READY);
        } catch (FeignException.NotFound | FeignException.Conflict e) {
            order.setErrorStatus(OrderStatus.FAIL, e.getMessage());
            throw new OrderCreateException(e.getMessage());
        }
        return paymentProviderHandler.ready(request, order.getId(), userId);
    }

    public ConfirmResult confirmPayment(PaymentProvider provider, Map<String, Object> param) {
        return paymentProviderHandler.confirm(provider, param);
    }
}
