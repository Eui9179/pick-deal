package com.leui.orderconsumer.domain.prcessedevent.aop;

import aop.IdempotentEventPayload;
import com.leui.orderconsumer.domain.prcessedevent.entity.ProcessedEvent;
import com.leui.orderconsumer.domain.prcessedevent.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Aspect
@Component
@Slf4j
public class TransactionalIdempotentEventAspect {

    private final ProcessedEventRepository processedEventRepository;
    private final PlatformTransactionManager transactionManager;

    @Around(
            value = "@annotation(idempotentEvent) && args(payload, ..)",
            argNames = "joinPoint,idempotentEvent,payload"
    )
    public Object around(
            ProceedingJoinPoint joinPoint,
            TransactionalIdempotentEvent idempotentEvent,
            IdempotentEventPayload payload
    ) {

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            if (processedEventRepository.existsById(payload.eventId())) {
                log.error("Event duplication error. eventId={}, orderId={}, orderStatus={}",
                        payload.eventId(),
                        payload.topic(),
                        payload.topicKey()
                );
                return null;
            }

            Object result = joinPoint.proceed();

            processedEventRepository.save(new ProcessedEvent(
                    payload.eventId(), payload.topic(), payload.topicKey(), LocalDateTime.now())
            );
            transactionManager.commit(status);

            return result;
        } catch (Throwable e) {
            log.error("TransactionalIdempotentEventAspect exception : {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
