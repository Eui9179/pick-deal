package com.leui.orderconsumer.domain.order.repository;

import com.leui.orderconsumer.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
