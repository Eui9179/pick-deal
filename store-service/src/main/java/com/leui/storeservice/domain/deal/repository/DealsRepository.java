package com.leui.storeservice.domain.deal.repository;

import com.leui.storeservice.domain.deal.entity.Deals;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DealsRepository extends JpaRepository<Deals, Long> {
    Slice<Deals> findByOrderByCreatedAtDesc(Pageable pageable); // 임시
}
