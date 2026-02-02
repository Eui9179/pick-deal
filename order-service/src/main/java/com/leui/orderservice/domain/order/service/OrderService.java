package com.leui.orderservice.domain.order.service;

import com.leui.orderservice.domain.order.dto.OrderCreateRequest;
import com.leui.orderservice.domain.order.dto.OrderDetailResponse;
import com.leui.orderservice.domain.order.entity.Order;
import com.leui.orderservice.domain.order.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public Order getOrder(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order Not Found. id: " + id));
    }

    public Order createOrder(Long userId, OrderCreateRequest request, BigDecimal totalAmount) {
        Order order = new Order(userId, request.dealId(), request.pickupTime(), request.quantity(), request.provider(), totalAmount);
        return orderRepository.save(order);
    }

    public OrderDetailResponse getOrderDetail(Long orderId) {
        return new OrderDetailResponse(orderId);
    }

}
