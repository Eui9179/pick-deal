package com.leui.storeservice.domain.store.repository;

import com.leui.storeservice.domain.store.entity.Stores;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoresRepository extends JpaRepository<Stores, Long> {
}
