package com.leui.orderservice.service;

import com.leui.orderservice.dto.OrderRequest;
import com.leui.orderservice.dto.OrderResponse;

public interface OrdersService {
    OrderResponse createOrder(OrderRequest orderRequest);
}
