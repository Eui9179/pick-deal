package com.leui.storeservice.domain.deal.repository;

import com.leui.storeservice.domain.deal.entity.DealReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DealReservationRepository extends JpaRepository<DealReservation, Long> {

    @Query("SELECT COALESCE(SUM(dr.quantity), 0) " +
           "FROM DealReservation dr " +
           "WHERE dr.dealId = :dealId")
    Long sumQuantityByDealId(@Param("dealId") Long dealId);

    List<DealReservation> findByExpiredAtLessThan(long expiredAt);

    @Modifying
    @Query("UPDATE FROM DealReservation dr SET dr.deletedAt = :now WHERE dr.expiredAt < :expiredAt AND dr.deletedAt IS NULL")
    int cleanupByExpiredAtBefore(@Param("expiredAt") long expiredAt, @Param("now")LocalDateTime now);

    @Modifying
    @Query("DELETE FROM DealReservation dr WHERE dr.orderId = :orderId")
    void deleteByOrderId(@Param("orderId") String orderId);

}
