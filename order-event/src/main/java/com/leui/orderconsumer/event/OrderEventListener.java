package com.leui.orderconsumer.event;

import com.leui.orderconsumer.domain.Order;
import com.leui.orderconsumer.domain.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Component
public class OrderEventListener {

    private final OrderRepository orderRepository;

    @Transactional
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
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Not found. id = " + event.getOrderId()));
        order.updateOrderExpired();
        acknowledgment.acknowledge();
    }

}
