package com.leui.storeevent.domain.deal.repository;

import com.leui.storeevent.domain.deal.entity.DealReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealReservationRepository extends JpaRepository<DealReservation, Long> {
    @Modifying
    @Query("DELETE FROM DealReservation dr WHERE dr.orderId = :orderId")
    void deleteByOrderId(@Param("orderId") String orderId);

    List<DealReservation> findByExpiredAtLessThan(long expiredAt);
}
