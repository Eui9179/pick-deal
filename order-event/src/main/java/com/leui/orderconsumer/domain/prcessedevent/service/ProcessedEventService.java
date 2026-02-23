package com.leui.orderconsumer.domain.prcessedevent.service;

import aop.IdempotentEventPayload;
import com.leui.orderconsumer.domain.prcessedevent.entity.ProcessedEvent;
import com.leui.orderconsumer.domain.prcessedevent.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProcessedEventService {

    private final ProcessedEventRepository processedEventRepository;

    @Transactional
    public Object execute(
            ProceedingJoinPoint joinPoint,
            IdempotentEventPayload payload
    ) {
        try {
            // 이미 처리된 이벤트는 무시
            if (processedEventRepository.existsById(payload.eventId())) {
                log.error("Event duplication error. eventId={}, topic={}, topicKey={}",
                        payload.eventId(),
                        payload.topic(),
                        payload.topicKey()
                );
                return null;
            }

            try { // 중복 저장 방지
                processedEventRepository.save(new ProcessedEvent(
                        payload.eventId(), payload.topic(), payload.topicKey(), LocalDateTime.now())
                );

            } catch (DataIntegrityViolationException e) {
                return null;
            }

            return joinPoint.proceed();
        } catch (Throwable e) {
            log.error("TransactionalIdempotentEventAspect exception : {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
