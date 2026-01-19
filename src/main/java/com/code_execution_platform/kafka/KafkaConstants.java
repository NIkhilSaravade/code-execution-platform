package com.code_execution_platform.kafka;

public final class KafkaConstants {

    private KafkaConstants() {}

    public static final String SUBMISSION_TOPIC = "submission-events";
    public static final String DLQ_TOPIC = "submission-events-dlq";

    public static final int MAX_RETRIES = 3;
    public static final String RETRY_HEADER = "retry-count";
}
