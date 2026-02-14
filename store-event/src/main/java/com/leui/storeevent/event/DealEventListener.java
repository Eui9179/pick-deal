package com.leui.storeevent.event;

import com.leui.storeevent.repository.DealRepository;
import com.leui.storeevent.repository.DealReservationRepository;
import exception.OutOfStockException;
import jakarta.persistence.EntityNotFoundException;
import kafka.event.PaymentDoneEvent;
import kafka.event.PaymentFailEvent;
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
public class DealEventListener {

    private final DealRepository dealRepository;
    private final DealReservationRepository dealReservationRepository;

    @KafkaListener(
            topics = EventTopics.PAYMENT_APPROVE,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onPaymentDoneEvent(
            @Payload PaymentDoneEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment
    ) {
        try {
            int stockQuantity = dealRepository.decreaseStockQuantity(event.getDealId(), event.getQuantity());
            if (stockQuantity == 0) {
                if (!dealRepository.existsById(event.getDealId())) {
                    throw new EntityNotFoundException("Deal Not Found. dealId: " + event.getDealId());
                }
                throw new OutOfStockException("Out of Stock. dealId: " + event.getDealId());
            }

            dealReservationRepository.deleteByOrderId(event.getOrderId());
        } catch (Exception e) {
            log.error("이벤트 처리 실패: orderId={}, status={}", event.getOrderId(), EventTopics.PAYMENT_APPROVE, e);
            throw e;
        }
    }

    @KafkaListener(
            topics = {EventTopics.PAYMENT_FAILED, EventTopics.PAYMENT_CANCELED},
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onPaymentFailEvent(
            @Payload PaymentFailEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment
    ) {
        try {
            log.info("이벤트 수신: partition={}, offset={}, orderId={}", partition, offset, event.getOrderId());
            dealReservationRepository.deleteByOrderId(event.getOrderId());
            acknowledgment.acknowledge();
            log.info("이벤트 처리 완료: orderId={}, status={}", event.getOrderId(), EventTopics.PAYMENT_FAILED);
        } catch (Exception e) {
            log.error("이벤트 처리 실패: orderId={}, status={}", event.getOrderId(), EventTopics.PAYMENT_FAILED, e);
            throw e;
        }
    }

}
