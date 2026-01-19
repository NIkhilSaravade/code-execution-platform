package com.code_execution_platform.repository;

import com.code_execution_platform.model.ExecutionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExecutionResultRepository
        extends JpaRepository<ExecutionResult, UUID> {

    Optional<ExecutionResult> findBySubmissionId(UUID submissionId);
}

