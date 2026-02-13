package com.leui.storeservice.domain.deal.scheduler;

import com.leui.storeservice.domain.deal.entity.DealReservation;
import com.leui.storeservice.domain.deal.repository.DealReservationRepository;
import kafka.event.DealReservationExpiredEvent;
import kafka.topic.EventTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class DealReservationScheduler {

    private final DealReservationRepository dealReservationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 만료된 재고 예약 정리
     * - 15분마다 실행
     * - expired_at < 현재시간인 예약 삭제
     */
    @Scheduled(cron = "0 */15 * * * *")
    @Transactional
    public void cleanupExpiredReservations() {
        long now = Instant.now().toEpochMilli();

        List<DealReservation> expired = dealReservationRepository.findByExpiredAtLessThan(now);

        if (expired.isEmpty()) {
            return;
        }

        expired.forEach(reservation -> {
            reservation.delete();
            kafkaTemplate.send(
                    EventTopics.DEAL_STOCK_RESERVATION_EXPIRED,
                    DealReservationExpiredEvent.builder()
                            .orderId(reservation.getOrderId())
                            .dealId(reservation.getDealId())
                            .userId(reservation.getUserId())
                            .quantity(reservation.getQuantity())
                            .expiredAt(reservation.getExpiredAt())
                            .build()
            );
        });
    }
}
