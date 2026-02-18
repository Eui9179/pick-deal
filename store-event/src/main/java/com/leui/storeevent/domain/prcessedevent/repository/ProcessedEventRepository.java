package com.leui.storeevent.domain.prcessedevent.repository;

import com.leui.storeevent.domain.prcessedevent.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {
}
