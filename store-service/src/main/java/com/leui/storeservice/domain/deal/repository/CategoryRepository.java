package com.leui.storeservice.domain.deal.repository;

import com.leui.storeservice.domain.deal.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
