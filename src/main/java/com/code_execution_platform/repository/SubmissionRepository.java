package com.code_execution_platform.repository;

import com.code_execution_platform.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubmissionRepository
        extends JpaRepository<Submission, UUID> {
}

