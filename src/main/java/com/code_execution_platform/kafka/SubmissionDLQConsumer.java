package com.code_execution_platform.kafka;

import com.code_execution_platform.event.SubmissionEvent;
import com.code_execution_platform.model.Submission;
import com.code_execution_platform.model.SubmissionStatus;
import com.code_execution_platform.repository.SubmissionRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class SubmissionDLQConsumer {

    private final ObjectMapper objectMapper;
    private final SubmissionRepository submissionRepository;

    public SubmissionDLQConsumer(
            ObjectMapper objectMapper,
            SubmissionRepository submissionRepository
    ) {
        this.objectMapper = objectMapper;
        this.submissionRepository = submissionRepository;
    }

    @KafkaListener(
            topics = KafkaConstants.DLQ_TOPIC,
            groupId = "code-execution-dlq-workers"
    )
    public void consumeDLQ(String message) {

        try {
            SubmissionEvent event =
                    objectMapper.readValue(message, SubmissionEvent.class);

            Submission submission =
                    submissionRepository.findById(event.getSubmissionId())
                            .orElseThrow(() ->
                                    new RuntimeException("Submission not found")
                            );

            submission.setStatus(SubmissionStatus.FAILED);
            submissionRepository.save(submission);

            // In real systems: alert, log, metrics
            System.err.println(
                    "Submission moved to DLQ: " + submission.getId()
            );

        } catch (Exception e) {
            // DLQ consumer should NEVER throw
            e.printStackTrace();
        }
    }
}

