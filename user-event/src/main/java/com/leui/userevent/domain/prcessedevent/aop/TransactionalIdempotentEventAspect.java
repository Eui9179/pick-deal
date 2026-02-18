package com.leui.userevent.domain.prcessedevent.aop;

import aop.IdempotentEventPayload;
import com.leui.userevent.domain.prcessedevent.service.ProcessedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Aspect
@Component
@Slf4j
public class TransactionalIdempotentEventAspect {

    private final ProcessedEventService processedEventService;

    @Around(
            value = "@annotation(idempotentEvent) && args(payload, ..)",
            argNames = "joinPoint,idempotentEvent,payload"
    )
    public Object around(
            ProceedingJoinPoint joinPoint,
            TransactionalIdempotentEvent idempotentEvent,
            IdempotentEventPayload payload
    ) {
        return processedEventService.execute(joinPoint, payload);
    }

}
