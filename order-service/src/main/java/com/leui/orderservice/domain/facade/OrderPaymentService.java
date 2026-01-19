package com.leui.orderservice.domain.facade;

import com.leui.orderservice.domain.order.dto.OrderCreateRequest;
import com.leui.orderservice.domain.order.entity.Order;
import com.leui.orderservice.domain.order.entity.OrderStatus;
import com.leui.orderservice.domain.order.service.OrderService;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.provider.ConfirmResult;
import com.leui.orderservice.domain.payments.provider.PaymentProviderHandler;
import com.leui.orderservice.global.feignclient.StoreDealFeignClient;
import dto.store.DealStockDecreaseRequest;
import enumtype.PaymentProvider;
import feign.FeignException;
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
        } catch (FeignException.NotFound e) {
            order.setStatus(OrderStatus.FAIL);
            order.setFailDescription("Deal entity not found.");
        } catch (FeignException.Conflict e) {
            order.setStatus(OrderStatus.FAIL);
            order.setFailDescription("Out of Stock.");
            throw new RuntimeException(); // TODO
        }
        return paymentProviderHandler.ready(request, order.getId(), userId);
    }

    public ConfirmResult confirmPayment(PaymentProvider provider, Map<String, Object> param) {
        return paymentProviderHandler.confirm(provider, param);
    }
}
