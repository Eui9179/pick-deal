package com.leui.orderservice.domain.order.service;

import com.leui.orderservice.domain.order.dto.OrderCreateRequest;
import com.leui.orderservice.domain.order.dto.OrderDetailResponse;
import com.leui.orderservice.domain.order.dto.OrderStatusResponse;
import com.leui.orderservice.domain.order.entity.Order;
import enumtype.OrderStatus;
import com.leui.orderservice.domain.order.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderService {

    private OrderRepository orderRepository;

    public Order getOrder(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order Not Found. id: " + id));
    }

    public Order createOrder(Long userId, OrderCreateRequest request) {
        Order order = new Order(userId, request.dealId(), request.quantity(), request.provider());
        return orderRepository.save(order);
    }

    public OrderDetailResponse getOrderDetail(Long orderId) {
        return new OrderDetailResponse(orderId);
    }

}
