package com.leui.storeservice.domain.deal.scheduler;

import com.leui.storeservice.domain.deal.repository.DealReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Component
public class DealReservationScheduler {

    private final DealReservationRepository dealReservationRepository;

    /**
     * 만료된 재고 예약 정리
     * - 15분마다 실행
     * - expired_at < 현재시간인 예약 삭제
     */
    @Scheduled(cron = "0 */15 * * * *")
    @Transactional
    public void cleanupExpiredReservations() {
        long now = Instant.now().toEpochMilli();

        int deleted = dealReservationRepository.deleteByExpiredAtBefore(now);

        if (deleted > 0) {
            log.info("Cleaned up {} expired deal reservations", deleted);
        }
    }
}
