package com.leui.userevent.domain.user.event;

import com.leui.protobuf.DealStockCommitEvent;
import com.leui.protobuf.UserPointAppliedEvent;
import com.leui.protobuf.UserPointAppliedFailEvent;
import com.leui.userevent.domain.user.dto.UserEvent;
import com.leui.userevent.domain.user.service.UserService;
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
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Component
public class UserEventListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserService userService;

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
            userService.applyUserPoint(new UserEvent(
                    event.getEventId(),
                    EventTopics.DEAL_STOCK_COMMIT,
                    event.getOrderId(),
                    event.getUserId(),
                    new BigDecimal(event.getTotalAmount()),
                    new BigDecimal(event.getUsedPoint())
            ));
            kafkaTemplate.send(EventTopics.USER_POINT_APPLIED, event.getOrderId(),
                    UserPointAppliedEvent.newBuilder()
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
            kafkaTemplate.send(EventTopics.USER_POINT_APPLIED_FAIL, event.getOrderId(),
                    UserPointAppliedFailEvent.newBuilder()
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
}
