package com.leui.userevent.domain.user.dto;

import aop.IdempotentEventPayload;

import java.math.BigDecimal;

public record UserEvent(
        String eventId,
        String topic,
        String topicKey,
        Long userId,
        BigDecimal totalAmount,
        BigDecimal usedPoint
) implements IdempotentEventPayload {
}
