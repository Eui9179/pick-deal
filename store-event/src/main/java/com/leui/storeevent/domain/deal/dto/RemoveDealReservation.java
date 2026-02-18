package com.leui.storeevent.domain.deal.dto;

import aop.IdempotentEventPayload;

public record RemoveDealReservation(
        String eventId,
        String topic,
        String topicKey,
        String orderId
) implements IdempotentEventPayload {
}
