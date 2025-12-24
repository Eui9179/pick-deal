package com.leui.storeservice.domain.deal.repository;

import com.leui.storeservice.domain.deal.entity.DealCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DealCategoryRepository extends JpaRepository<DealCategory, Long> {
}
