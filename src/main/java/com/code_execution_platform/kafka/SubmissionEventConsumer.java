package com.code_execution_platform.kafka;

;
import com.code_execution_platform.event.SubmissionEvent;
import com.code_execution_platform.model.ExecutionResult;
import com.code_execution_platform.model.Submission;
import com.code_execution_platform.model.SubmissionStatus;
import com.code_execution_platform.repository.ExecutionResultRepository;
import com.code_execution_platform.repository.SubmissionRepository;
import com.code_execution_platform.service.DockerExecutionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
public class SubmissionEventConsumer {

    private final ObjectMapper objectMapper;
    private final SubmissionRepository submissionRepository;
    private final ExecutionResultRepository executionResultRepository;
    private final DockerExecutionService dockerExecutionService;

    public SubmissionEventConsumer(
            ObjectMapper objectMapper,
            SubmissionRepository submissionRepository,
            ExecutionResultRepository executionResultRepository,
            DockerExecutionService dockerExecutionService
    ) {
        this.objectMapper = objectMapper;
        this.submissionRepository = submissionRepository;
        this.executionResultRepository = executionResultRepository;
        this.dockerExecutionService = dockerExecutionService;
    }

    @KafkaListener(
            topics = "submission-events",
            groupId = "code-execution-workers"
    )
    public void consume(String message) {

        try {
            // 1. Deserialize event
            SubmissionEvent event =
                    objectMapper.readValue(message, SubmissionEvent.class);

            UUID submissionId = event.getSubmissionId();

            // 2. Load submission
            Submission submission =
                    submissionRepository.findById(submissionId)
                            .orElseThrow(() ->
                                    new RuntimeException("Submission not found: " + submissionId)
                            );

            submission.setStatus(SubmissionStatus.RUNNING);
            submissionRepository.save(submission);

            // 3. Execute code in Docker
            ExecutionResult result =
                    dockerExecutionService.executePythonInDocker(
                            submission.getSourceCode(),
                            submissionId
                    );

            // 4. Save result
            executionResultRepository.save(result);

            // 5. Mark completed
            submission.setStatus(SubmissionStatus.COMPLETED);
            submissionRepository.save(submission);

        } catch (Exception e) {
            // IMPORTANT: In real systems we would retry / send to DLQ
            e.printStackTrace();
        }
    }
}

