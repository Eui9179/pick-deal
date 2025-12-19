package com.leui.dealservice.repository;

import com.leui.dealservice.entity.Deals;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DealsRespository extends JpaRepository<Deals, Integer> {
}
