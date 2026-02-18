package com.leui.orderconsumer.domain.prcessedevent.repository;

import com.leui.orderconsumer.domain.prcessedevent.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {
}
