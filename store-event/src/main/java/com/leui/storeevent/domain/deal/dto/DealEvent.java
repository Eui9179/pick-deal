package com.leui.storeevent.domain.deal.dto;

import aop.IdempotentEventPayload;

public record DealEvent(
        String eventId,
        String topic,
        String topicKey,
        Long dealId,
        Integer quantity,
        String orderId
) implements IdempotentEventPayload {
}
