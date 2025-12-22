package com.leui.orderservice.service;

import com.leui.orderservice.dto.OrderCreateRequest;
import com.leui.orderservice.dto.OrderCreateResponse;
import com.leui.orderservice.dto.OrderDetailResponse;
import com.leui.orderservice.dto.OrderStatusResponse;

public interface OrdersService {
    OrderCreateResponse createOrder(OrderCreateRequest orderCreateRequest);

    OrderDetailResponse getOrderDetail(Long orderId);

    OrderStatusResponse getOrderStatus(Long orderId);
}
