package com.leui.storeservice.domain.deal.repository;

import com.leui.storeservice.domain.deal.entity.Deals;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealsRepository extends JpaRepository<Deals, Long> {
    List<Deals> findDealsByStoreIdOrderByCreatedAtDesc(Long storeId);
}
