package com.leui.storeservice.domain.deal.event;

import com.leui.storeservice.domain.deal.service.DealService;
import kafka.event.PaymentDoneEvent;
import kafka.topic.KafkaTopics;
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

    private final DealService dealService;

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_DONE,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onOrderEvent(
            @Payload PaymentDoneEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment
    ) {
        try {
            log.info("이벤트 수신: partition={}, offset={}, orderId={}", partition, offset, event.getOrderId());
            dealService.confirmStock(event.getDealId(), event.getOrderId(), event.getQuantity());
            acknowledgment.acknowledge();
            log.info("이벤트 처리 완료: orderId={}, status={}", event.getOrderId(), event.getStatus());
        } catch (Exception e) {
            log.error("이벤트 처리 실패: orderId={}, status={}", event.getOrderId(), event.getStatus(), e);
            throw e;
        }
    }

}
