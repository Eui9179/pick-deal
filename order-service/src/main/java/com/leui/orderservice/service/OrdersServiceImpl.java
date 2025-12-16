package com.leui.orderservice.service;

import com.leui.orderservice.dto.OrderRequest;
import com.leui.orderservice.dto.OrderResponse;
import org.springframework.stereotype.Service;

@Service
public class OrdersServiceImpl implements OrdersService {
    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {
        return OrderResponse.builder()
                .orderId(1L).build();
    }
}
