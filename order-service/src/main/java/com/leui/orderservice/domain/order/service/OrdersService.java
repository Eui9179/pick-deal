package com.leui.orderservice.domain.order.service;

import com.leui.orderservice.domain.order.dto.OrderCreateRequest;
import com.leui.orderservice.domain.order.dto.OrderCreateResponse;
import com.leui.orderservice.domain.order.dto.OrderDetailResponse;
import com.leui.orderservice.domain.order.dto.OrderStatusResponse;
import com.leui.orderservice.domain.order.entity.OrderStatus;
import org.springframework.stereotype.Service;

@Service
public class OrdersService {

    public OrderCreateResponse createOrder(OrderCreateRequest orderCreateRequest) {
        return new OrderCreateResponse(1L);
    }

    public OrderDetailResponse getOrderDetail(Long orderId) {
        return new OrderDetailResponse(orderId);
    }

    public OrderStatusResponse getOrderStatus(Long orderId) {
        return new OrderStatusResponse(orderId, OrderStatus.PAID);
    }

    public OrderStatusResponse updateOrderStatusPaid(Long orderId) {
        return new OrderStatusResponse(orderId, OrderStatus.PAID);
    }

}
