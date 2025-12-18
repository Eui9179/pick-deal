package com.leui.orderservice.service;

import com.leui.orderservice.dto.OrderCreateRequest;
import com.leui.orderservice.dto.OrderCreateResponse;

public interface OrdersService {
    OrderCreateResponse createOrder(OrderCreateRequest orderCreateRequest);
}
