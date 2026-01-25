package com.leui.storeservice.domain.deal.repository;

import com.leui.storeservice.domain.deal.entity.DealReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface DealReservationRepository extends JpaRepository<DealReservation, Long> {

    @Query("SELECT COALESCE(SUM(dr.quantity), 0) " +
           "FROM DealReservation dr " +
           "WHERE dr.dealId = :dealId")
    Long sumQuantityByDealId(@Param("dealId") Long dealId);

    @Modifying
    @Query("DELETE FROM DealReservation dr WHERE dr.expiredAt < :expiredAt")
    int deleteByExpiredAtBefore(@Param("expiredAt") long expiredAt);

    @Modifying
    @Query("DELETE FROM DealReservation dr WHERE dr.orderId = :orderId")
    void deleteByOrderId(@Param("orderId") String orderId);

    @Modifying
    @Query("DELETE FROM DealReservation d WHERE d.orderId IN :ids")
    void deleteByIds(@Param("ids") Set<String> ids);
}
