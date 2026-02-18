package com.leui.orderconsumer.domain.order.dto;

import aop.IdempotentEventPayload;
import enumtype.OrderStatus;

public record OrderEvent(
        String eventId,
        String topic,
        String topicKey
) implements IdempotentEventPayload {
}
