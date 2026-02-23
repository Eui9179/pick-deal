package com.leui.orderconsumer.domain.order.service;

import com.leui.orderconsumer.domain.order.dto.OrderEvent;
import com.leui.orderconsumer.domain.order.entity.Order;
import com.leui.orderconsumer.domain.order.repository.OrderRepository;
import com.leui.orderconsumer.domain.prcessedevent.aop.TransactionalIdempotentEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @TransactionalIdempotentEvent
    public void updateStatus(OrderEvent event) {
        Order order = getOrder(event.topicKey());
        order.setStatus(event.status());
    }

    public Order getOrder(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found. id = " + id));
    }

}
