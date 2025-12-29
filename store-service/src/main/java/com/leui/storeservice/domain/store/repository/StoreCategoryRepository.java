package com.leui.storeservice.domain.store.repository;

import com.leui.storeservice.domain.store.entity.StoreCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreCategoryRepository extends JpaRepository<StoreCategory, Long> {
}
