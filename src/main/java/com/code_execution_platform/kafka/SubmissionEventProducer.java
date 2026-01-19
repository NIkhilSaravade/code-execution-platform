package com.code_execution_platform.kafka;

import com.code_execution_platform.event.SubmissionEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class SubmissionEventProducer {

    private static final String TOPIC = "submission-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public SubmissionEventProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishSubmissionEvent(SubmissionEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, event.getSubmissionId().toString(), payload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize submission event", e);
        }
    }
}
