package com.leui.userevent.event;

import com.leui.userevent.repository.User;
import com.leui.userevent.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import kafka.event.DealStockCommitEvent;
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

import java.math.BigDecimal;

@RequiredArgsConstructor
@Slf4j
@Component
public class UserEventListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserRepository userRepository;

    @Transactional
    @KafkaListener(
            topics = EventTopics.DEAL_STOCK_COMMIT,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onDealStockCommitEvent(
            @Payload DealStockCommitEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment
    ) {
        try {
            User user = userRepository.findById(event.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("Not found. id = {}" + event.getUserId()));
            user.subtractPoint(event.getUsedPoint()); // 포인트 사용
            BigDecimal earn = event.getTotalAmount().subtract(event.getUsedPoint()).divide(BigDecimal.TEN);
            user.accumulatePoint(earn); // 포인트 적립
            kafkaTemplate.send(EventTopics.USER_POINT_HANDLE_DONE, event.getOrderId(),
                    DealStockCommitEvent.builder()
                            .orderId(event.getOrderId())
                            .dealId(event.getDealId())
                            .userId(event.getUserId())
                            .quantity(event.getQuantity())
                            .totalAmount(event.getTotalAmount())
                            .usedPoint(event.getUsedPoint())
                            .paymentKey(event.getPaymentKey())
                            .build());
        } catch (Exception e) {
            kafkaTemplate.send(EventTopics.USER_POINT_HANDLE_DONE, event.getOrderId(),
                    DealStockCommitEvent.builder()
                            .orderId(event.getOrderId())
                            .dealId(event.getDealId())
                            .userId(event.getUserId())
                            .quantity(event.getQuantity())
                            .totalAmount(event.getTotalAmount())
                            .usedPoint(event.getUsedPoint())
                            .paymentKey(event.getPaymentKey())
                            .build());
        }
    }
}
