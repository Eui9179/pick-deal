package com.leui.storeevent.event;

import com.leui.storeevent.repository.DealRepository;
import com.leui.storeevent.repository.DealReservationRepository;
import exception.OutOfStockException;
import jakarta.persistence.EntityNotFoundException;
import kafka.event.DealStockCommitEvent;
import kafka.event.PaymentApproveEvent;
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

@RequiredArgsConstructor
@Slf4j
@Component
public class DealEventListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final DealRepository dealRepository;
    private final DealReservationRepository dealReservationRepository;

    @KafkaListener(
            topics = EventTopics.PAYMENT_APPROVE,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onPaymentApproveEvent(
            @Payload PaymentApproveEvent event,
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

            kafkaTemplate.send(EventTopics.DEAL_STOCK_COMMIT, event.getOrderId(),
                    DealStockCommitEvent.builder()
                            .orderId(event.getOrderId())
                            .dealId(event.getDealId())
                            .userId(event.getUserId())
                            .quantity(event.getQuantity())
                            .totalAmount(event.getTotalAmount())
                            .usedPoint(event.getUsedPoint())
                            .paymentKey(event.getPaymentKey())
                            .build()
            );
        } catch (Exception e) {
            log.error("이벤트 처리 실패: orderId={}, status={}", event.getOrderId(), EventTopics.DEAL_STOCK_COMMIT_FAIL, e);
            kafkaTemplate.send(EventTopics.DEAL_STOCK_COMMIT_FAIL, event.getOrderId(),
                    DealStockCommitEvent.builder()
                            .orderId(event.getOrderId())
                            .dealId(event.getDealId())
                            .userId(event.getUserId())
                            .quantity(event.getQuantity())
                            .totalAmount(event.getTotalAmount())
                            .usedPoint(event.getUsedPoint())
                            .paymentKey(event.getPaymentKey())
                            .build());
            throw e;
        }
    }

    @KafkaListener(
            topics = {EventTopics.PAYMENT_APPROVE_FAIL, EventTopics.PAYMENT_CANCELED},
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
            log.info("이벤트 처리 완료: orderId={}, status={}", event.getOrderId(), EventTopics.PAYMENT_APPROVE_FAIL);
        } catch (Exception e) {
            log.error("이벤트 처리 실패: orderId={}, status={}", event.getOrderId(), EventTopics.PAYMENT_APPROVE_FAIL, e);
            throw e;
        }
    }

}
