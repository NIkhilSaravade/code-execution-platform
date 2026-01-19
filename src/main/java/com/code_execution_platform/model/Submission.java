package com.code_execution_platform.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    private UUID id;

    private String language;

    @Column(columnDefinition = "TEXT")
    private String sourceCode;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;

    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.status = SubmissionStatus.QUEUED;
    }

    // getters & setters
}

