package com.leui.orderservice.service;

import com.leui.orderservice.dto.OrderCreateRequest;
import com.leui.orderservice.dto.OrderCreateResponse;
import com.leui.orderservice.dto.OrderDetailResponse;
import com.leui.orderservice.dto.OrderStatusResponse;
import com.leui.orderservice.entity.OrderStatus;
import org.springframework.stereotype.Service;

@Service
public class OrdersServiceImpl implements OrdersService {
    @Override
    public OrderCreateResponse createOrder(OrderCreateRequest orderCreateRequest) {
        return OrderCreateResponse.builder()
                .orderId(1L).build();
    }

    @Override
    public OrderDetailResponse getOrderDetail(Long orderId) {
        return OrderDetailResponse.builder().orderId(orderId).build();
    }

    @Override
    public OrderStatusResponse getOrderStatus(Long orderId) {
        return OrderStatusResponse.builder().orderId(orderId).status(OrderStatus.PAID).build();
    }
}
