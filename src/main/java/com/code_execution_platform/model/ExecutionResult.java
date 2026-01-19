package com.code_execution_platform.model;

import com.code_execution_platform.model.enums.ExecutionVerdict;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "execution_results")
public class ExecutionResult {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID submissionId;

    private long executionTimeMs;

    private int exitCode;

    @Column(columnDefinition = "TEXT")
    private String stdout;

    @Column(columnDefinition = "TEXT")
    private String stderr;

    @Enumerated(EnumType.STRING)
    private ExecutionVerdict verdict;

    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    // getters and setters
    public UUID getId() {
        return id;
    }

    public UUID getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(UUID submissionId) {
        this.submissionId = submissionId;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public ExecutionVerdict getVerdict() {
        return verdict;
    }

    public void setVerdict(ExecutionVerdict verdict) {
        this.verdict = verdict;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

