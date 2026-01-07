package com.leui.storeservice.domain.deal.repository;

import com.leui.storeservice.domain.deal.entity.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealRepository extends JpaRepository<Deal, Long> {
    @Query("""
            select deal from Deal deal
            join fetch deal.discountPolicy
            where deal.store.id = :storeId
            order by deal.createdAt desc
            """)
    List<Deal> findAllByStoreIdWithDiscountPolicy(Long storeId);

    @Query("""
            select deal from Deal deal
            join fetch deal.discountPolicy
            where deal.id = :dealId
            """)
    Deal findByIdWithDiscountPolicy(Long dealId);
}
