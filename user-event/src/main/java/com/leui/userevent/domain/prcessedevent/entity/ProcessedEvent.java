package com.leui.userevent.domain.prcessedevent.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Getter
public class ProcessedEvent {

    @Id
    private String id;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String topicKey;

    @Column(nullable = false)
    private LocalDateTime createdAt;

}
