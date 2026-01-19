package com.code_execution_platform.service;


import com.code_execution_platform.event.SubmissionEvent;
import com.code_execution_platform.kafka.SubmissionEventProducer;
import com.code_execution_platform.model.Submission;
import com.code_execution_platform.model.SubmissionStatus;
import com.code_execution_platform.repository.SubmissionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionEventProducer eventProducer;

    public SubmissionService(
            SubmissionRepository submissionRepository,
            SubmissionEventProducer eventProducer
    ) {
        this.submissionRepository = submissionRepository;
        this.eventProducer = eventProducer;
    }

    /**
     * Phase 3 – Async Submission Flow
     * - Save submission
     * - Publish event to Kafka
     * - Return immediately
     */
    public UUID submit(String language, String sourceCode) {

        Submission submission = new Submission();
        submission.setLanguage(language);
        submission.setSourceCode(sourceCode);
        submission.setStatus(SubmissionStatus.QUEUED);

        submissionRepository.save(submission);

        SubmissionEvent event =
                new SubmissionEvent(submission.getId(), language);

        eventProducer.publishSubmissionEvent(event);

        return submission.getId();
    }
}
