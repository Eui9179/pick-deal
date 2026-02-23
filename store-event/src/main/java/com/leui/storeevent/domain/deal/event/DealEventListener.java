package com.leui.storeevent.domain.deal.event;

import com.leui.protobuf.*;
import com.leui.storeevent.domain.deal.dto.DealEvent;
import com.leui.storeevent.domain.deal.dto.RemoveDealReservation;
import com.leui.storeevent.domain.deal.service.DealService;
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

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
public class DealEventListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final DealService dealService;

    /**
     * 임시 재고 삭제 및 재고 감소 반영
     */
    @KafkaListener(
            topics = EventTopics.PAYMENT_APPROVED,
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
            dealService.commitStockQuantity(new DealEvent(
                    event.getEventId(),
                    EventTopics.PAYMENT_APPROVED,
                    event.getOrderId(),
                    event.getDealId(),
                    event.getQuantity(),
                    event.getOrderId()
            ));
            acknowledgment.acknowledge();
            kafkaTemplate.send(EventTopics.DEAL_STOCK_COMMIT, event.getOrderId(),
                    DealStockCommitEvent.newBuilder()
                            .setEventId(UUID.randomUUID().toString())
                            .setOrderId(event.getOrderId())
                            .setDealId(event.getDealId())
                            .setUserId(event.getUserId())
                            .setQuantity(event.getQuantity())
                            .setTotalAmount(event.getTotalAmount())
                            .setUsedPoint(event.getUsedPoint())
                            .setPaymentKey(event.getPaymentKey())
                            .build());
        } catch (Exception e) {
            log.error("이벤트 처리 실패: orderId={}, status={}", event.getOrderId(), EventTopics.DEAL_STOCK_COMMIT_FAIL, e);
            kafkaTemplate.send(EventTopics.DEAL_STOCK_COMMIT_FAIL, event.getOrderId(),
                    DealStockCommitFailEvent.newBuilder()
                            .setEventId(UUID.randomUUID().toString())
                            .setOrderId(event.getOrderId())
                            .setDealId(event.getDealId())
                            .setUserId(event.getUserId())
                            .setQuantity(event.getQuantity())
                            .setTotalAmount(event.getTotalAmount())
                            .setUsedPoint(event.getUsedPoint())
                            .setPaymentKey(event.getPaymentKey())
                            .setProvider(event.getProvider())
                            .build());
            throw e;
        }
    }

    @KafkaListener(
            topics = {EventTopics.PAYMENT_APPROVED_FAIL, EventTopics.PAYMENT_CANCELED},
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
            dealService.removeDealReservation(new RemoveDealReservation(
                    event.getEventId(),
                    EventTopics.PAYMENT_APPROVED_FAIL,
                    event.getOrderId(),
                    event.getOrderId())
            );
            acknowledgment.acknowledge();
            log.info("이벤트 처리 완료: orderId={}, status={}", event.getOrderId(), EventTopics.PAYMENT_APPROVED_FAIL);
        } catch (Exception e) {
            log.error("이벤트 처리 실패: orderId={}, status={}", event.getOrderId(), EventTopics.PAYMENT_APPROVED_FAIL, e);
            throw e;
        }
    }

    @KafkaListener(
            topics = EventTopics.USER_POINT_APPLIED_FAIL,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onPaymentFailEvent(
            @Payload UserPointAppliedFailEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment
    ) {
        log.info("이벤트 수신: partition={}, offset={}, orderId={}", partition, offset, event.getOrderId());
        dealService.removeDealReservation(new RemoveDealReservation(
                event.getEventId(),
                EventTopics.USER_POINT_APPLIED_FAIL,
                event.getOrderId(),
                event.getOrderId())
        );
        acknowledgment.acknowledge();
        kafkaTemplate.send(EventTopics.DEAL_STOCK_COMMIT_FAIL, event.getOrderId(),
                DealStockCommitEvent.newBuilder()
                        .setEventId(UUID.randomUUID().toString())
                        .setOrderId(event.getOrderId())
                        .setDealId(event.getDealId())
                        .setUserId(event.getUserId())
                        .setQuantity(event.getQuantity())
                        .setTotalAmount(event.getTotalAmount())
                        .setUsedPoint(event.getUsedPoint())
                        .setPaymentKey(event.getPaymentKey())
                        .build());
    }

}
