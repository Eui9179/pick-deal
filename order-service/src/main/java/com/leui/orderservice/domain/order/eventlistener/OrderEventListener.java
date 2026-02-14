package com.leui.orderservice.domain.order.eventlistener;

import com.leui.orderservice.domain.order.entity.Order;
import com.leui.orderservice.domain.order.service.OrderService;
import enumtype.OrderStatus;
import kafka.event.DealReservationExpiredEvent;
import kafka.topic.EventTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class OrderEventListener {

    private final OrderService orderService;

    @KafkaListener(
            topics = EventTopics.DEAL_STOCK_RESERVATION_EXPIRED,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onPaymentDoneEvent(
            @Payload DealReservationExpiredEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment
    ) {
        log.info("이벤트 수신: partition={}, offset={}, orderId={}", partition, offset, event.getOrderId());
        Order order = orderService.getOrder(event.getOrderId());
        order.updateOrderExpired();
        acknowledgment.acknowledge();
    }

}
