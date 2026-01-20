package com.leui.orderservice.domain.facade;

import com.leui.orderservice.domain.order.dto.OrderCreateRequest;
import com.leui.orderservice.domain.order.entity.Order;
import enumtype.OrderStatus;
import com.leui.orderservice.domain.order.service.OrderService;
import com.leui.orderservice.domain.payments.dto.PaymentReadyResponse;
import com.leui.orderservice.domain.payments.provider.ConfirmResult;
import com.leui.orderservice.domain.payments.provider.PaymentProviderHandler;
import com.leui.orderservice.global.feignclient.StoreDealFeignClient;
import dto.store.DealStockDecreaseRequest;
import enumtype.PaymentProvider;
import exception.OutOfStockException;
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
        decreaseDealStock(order, request.dealId(), request.quantity());
        return paymentProviderHandler.ready(request, order.getId(), userId);
    }

    public ConfirmResult confirmPayment(PaymentProvider provider, Map<String, Object> param) {
        ConfirmResult confirm = paymentProviderHandler.confirm(provider, param);

        return confirm;
    }

    // FeignClient
    private void decreaseDealStock(Order order, Long dealId, int quantity) {
        try {
            storeDealFeignClient.decreaseDealStock(dealId, new DealStockDecreaseRequest(quantity));
            order.setStatus(OrderStatus.ORDER_READY);
        } catch (FeignException.NotFound e) {
            order.setErrorStatus(OrderStatus.FAIL_DEAL_NOTFOUND, e.getMessage());
            throw new EntityNotFoundException(e.getMessage());
        } catch (FeignException.Conflict e) {
            order.setErrorStatus(OrderStatus.FAIL_OUT_OF_STOCK, e.getMessage());
            throw new OutOfStockException(e.getMessage());
        }
    }
}
