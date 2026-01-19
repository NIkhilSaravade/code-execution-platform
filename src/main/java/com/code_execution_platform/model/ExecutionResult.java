package com.code_execution_platform.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
@Entity
@Table(name = "execution_results")
public class ExecutionResult {

    @Id
    private UUID id;

    private UUID submissionId;

    @Column(columnDefinition = "TEXT")
    private String stdout;

    @Column(columnDefinition = "TEXT")
    private String stderr;

    private Long executionTimeMs;

    private Integer exitCode;

    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    // getters & setters
}
