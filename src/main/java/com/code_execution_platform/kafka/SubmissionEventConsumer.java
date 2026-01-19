package com.code_execution_platform.kafka;

import com.code_execution_platform.event.SubmissionEvent;
import com.code_execution_platform.model.ExecutionResult;
import com.code_execution_platform.model.Submission;
import com.code_execution_platform.model.SubmissionStatus;
import com.code_execution_platform.model.enums.ExecutionVerdict;
import com.code_execution_platform.repository.ExecutionResultRepository;
import com.code_execution_platform.repository.SubmissionRepository;
import com.code_execution_platform.service.DockerExecutionService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.nio.ByteBuffer;
import java.util.UUID;

@Service
public class SubmissionEventConsumer {

    private final ObjectMapper objectMapper;
    private final SubmissionRepository submissionRepository;
    private final ExecutionResultRepository executionResultRepository;
    private final DockerExecutionService dockerExecutionService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public SubmissionEventConsumer(
            ObjectMapper objectMapper,
            SubmissionRepository submissionRepository,
            ExecutionResultRepository executionResultRepository,
            DockerExecutionService dockerExecutionService,
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.objectMapper = objectMapper;
        this.submissionRepository = submissionRepository;
        this.executionResultRepository = executionResultRepository;
        this.dockerExecutionService = dockerExecutionService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
            topics = KafkaConstants.SUBMISSION_TOPIC,
            groupId = "code-execution-workers"
    )
    public void consume(ConsumerRecord<String, String> record) {

        int retryCount = getRetryCount(record);

        try {
            SubmissionEvent event =
                    objectMapper.readValue(record.value(), SubmissionEvent.class);

            UUID submissionId = event.getSubmissionId();

            Submission submission =
                    submissionRepository.findById(submissionId)
                            .orElseThrow(() ->
                                    new RuntimeException("Submission not found")
                            );

            submission.setStatus(SubmissionStatus.RUNNING);
            submissionRepository.save(submission);

            ExecutionResult result =
                    dockerExecutionService.executePythonInDocker(
                            submission.getSourceCode(),
                            submissionId
                    );

            executionResultRepository.save(result);

            if (result.getVerdict() == ExecutionVerdict.SYSTEM_ERROR) {
                submission.setStatus(SubmissionStatus.FAILED);
            } else {
                submission.setStatus(SubmissionStatus.COMPLETED);
            }

            submissionRepository.save(submission);

        } catch (Exception e) {

            if (retryCount >= KafkaConstants.MAX_RETRIES) {
                // Next step: DLQ
                kafkaTemplate.send(
                        KafkaConstants.DLQ_TOPIC,
                        record.key(),
                        record.value()
                );
                return;
            }

            throw e; // retry
        }
    }

    private int getRetryCount(ConsumerRecord<String, String> record) {
        Header header = record.headers().lastHeader(KafkaConstants.RETRY_HEADER);
        if (header == null) {
            return 0;
        }
        return ByteBuffer.wrap(header.value()).getInt();
    }
}
