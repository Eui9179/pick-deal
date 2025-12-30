package com.leui.storeservice.domain.deal.repository;

import com.leui.storeservice.domain.deal.entity.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealRepository extends JpaRepository<Deal, Long> {
    List<Deal> findDealsByStoreIdOrderByCreatedAtDesc(Long storeId);
}
