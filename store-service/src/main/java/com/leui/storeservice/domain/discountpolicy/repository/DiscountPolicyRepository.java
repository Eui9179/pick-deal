package com.leui.storeservice.domain.discountpolicy.repository;

import com.leui.storeservice.domain.discountpolicy.entity.DiscountPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountPolicyRepository extends JpaRepository<DiscountPolicy, Long> {
}
