package com.leui.orderconsumer.event;

import com.leui.orderconsumer.repository.order.Order;
import com.leui.orderconsumer.repository.order.OrderRepository;
import com.leui.orderconsumer.repository.prcessedevent.ProcessedEvent;
import com.leui.orderconsumer.repository.prcessedevent.ProcessedEventRepository;
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

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Component
public class OrderEventListener {

    private final OrderRepository orderRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
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
        if (processedEventRepository.existsById(event.getEventId())) {
            log.info("이벤트 중복: eventId={}", event.getEventId());
            return;
        }
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Not found. id = " + event.getOrderId()));
        order.updateOrderComplete();


        processedEventRepository.save(
                new ProcessedEvent(
                        event.getEventId(),
                        event.getOrderId(),
                        OrderStatus.ORDER_COMPLETE,
                        LocalDateTime.now()
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
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Not found. id = " + event.getOrderId()));
        order.updateOrderExpired();
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
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Not found. id = " + event.getOrderId()));
        order.setStatus(OrderStatus.FAIL_DEAL_STOCK_COMMIT);
        // TODO PG 결제 취소 요청
        acknowledgment.acknowledge();

        kafkaTemplate.send(EventTopics.PAYMENT_APPROVED_FAIL, order.getId(), new PaymentFailEvent(order.getId()));
    }

}
