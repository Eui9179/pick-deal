package com.leui.storeservice.domain.deal.event;

import com.leui.storeservice.domain.deal.service.DealService;
import event.OrderEvent;
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
            topics = OrderEvent.TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onOrderEvent(
            @Payload OrderEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment
    ) {
        try {
            log.info("이벤트 수신: partition={}, offset={}, orderId={}", partition, offset, event.getOrderId());
            switch (event.getStatus()) {
                case ORDER_START ->
                        dealService.reserveStock(event.getDealId(), event.getOrderId(), event.getQuantity(), event.getUserId());
                case FAIL_PAYMENT_CANCELED,
                     FAIL_PAYMENT_ABORTED,
                     FAIL_PAYMENT_EXPIRED,
                     FAIL_REJECT_CARD_COMPANY -> dealService.rollbackStock(event.getOrderId(), event.getQuantity());
                case PAYMENT_DONE -> // TODO 알림 전송
                        dealService.confirmStock(event.getDealId(), event.getOrderId(), event.getQuantity());
                default -> log.debug("무시된 이벤트: orderId={}, status={}", event.getOrderId(), event.getStatus());
            }
            acknowledgment.acknowledge();
            log.info("이벤트 처리 완료: orderId={}, status={}", event.getOrderId(), event.getStatus());
        } catch (Exception e) {
            log.error("이벤트 처리 실패: orderId={}, status={}", event.getOrderId(), event.getStatus(), e);
            throw e;
        }
    }

}
