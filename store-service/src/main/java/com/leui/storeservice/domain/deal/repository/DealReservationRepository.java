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
    @Modifying
    @Query("DELETE FROM DealReservation d WHERE d.orderId IN :ids")
    void deleteByIds(@Param("ids") Set<String> ids);
}
