package com.leui.orderservice.domain.order.service;

import com.leui.orderservice.domain.order.dto.OrderCreateRequest;
import com.leui.orderservice.domain.order.dto.OrderDetailResponse;
import com.leui.orderservice.domain.order.entity.Order;
import com.leui.orderservice.domain.order.repository.OrderRepository;
import enumtype.OrderStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return orderRepository.save(new Order(
                userId,
                request.dealId(),
                request.pickupTime(),
                request.quantity(),
                request.provider(),
                request.usedPoint(),
                totalAmount));
    }

    public OrderDetailResponse getOrderDetail(Long orderId) {
        return new OrderDetailResponse(orderId);
    }

    @Transactional
    public void updateStatus(String orderId, OrderStatus orderStatus) {
        Order order = getOrder(orderId);
        order.setStatus(orderStatus);
    }
}
