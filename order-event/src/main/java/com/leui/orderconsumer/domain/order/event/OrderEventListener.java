package com.leui.orderconsumer.domain.order.event;

import com.leui.orderconsumer.domain.order.dto.OrderEvent;
import com.leui.orderconsumer.domain.order.entity.Order;
import com.leui.orderconsumer.domain.order.service.OrderService;
import enumtype.OrderStatus;
import jakarta.persistence.EntityNotFoundException;
import kafka.event.DealReservationExpiredEvent;
import kafka.event.DealStockCommitEvent;
import kafka.event.PaymentFailEvent;
import kafka.topic.EventTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
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

    private final OrderService orderService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(
            topics = EventTopics.USER_POINT_APPLIED,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onOrderCompleteEvent(
            @Payload DealReservationExpiredEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment
    ) {
        log.info("이벤트 수신: partition={}, offset={}, orderId={}", partition, offset, event.getOrderId());
        orderService.updateStatus(new OrderEvent(
                event.getEventId(),
                EventTopics.USER_POINT_APPLIED,
                event.getOrderId()
        ));
        acknowledgment.acknowledge();
    }

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
        orderService.updateStatus(new OrderEvent(
                event.getEventId(),
                EventTopics.DEAL_STOCK_RESERVATION_EXPIRED,
                event.getOrderId()
                ));
        acknowledgment.acknowledge();
    }

    @Transactional
    @KafkaListener(
            topics = EventTopics.DEAL_STOCK_COMMIT_FAIL,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onPaymentDoneEvent(
            @Payload DealStockCommitEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment
    ) {
        log.info("이벤트 수신: partition={}, offset={}, orderId={}", partition, offset, event.getOrderId());
        orderService.updateStatus(new OrderEvent(
                event.getEventId(),
                EventTopics.DEAL_STOCK_COMMIT_FAIL,
                event.getOrderId()
        ));
        // TODO PG 결제 취소 요청
        acknowledgment.acknowledge();
        kafkaTemplate.send(EventTopics.PAYMENT_APPROVED_FAIL, event.getOrderId(), new PaymentFailEvent(event.getOrderId()));
    }

}
