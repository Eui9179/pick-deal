package com.leui.userevent.domain.prcessedevent.repository;

import com.leui.userevent.domain.prcessedevent.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {
}
