package com.leui.orderservice.service;

import com.leui.orderservice.dto.OrderCreateRequest;
import com.leui.orderservice.dto.OrderCreateResponse;
import org.springframework.stereotype.Service;

@Service
public class OrdersServiceImpl implements OrdersService {
    @Override
    public OrderCreateResponse createOrder(OrderCreateRequest orderCreateRequest) {
        return OrderCreateResponse.builder()
                .orderId(1L).build();
    }
}
