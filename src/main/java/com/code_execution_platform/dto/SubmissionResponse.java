package com.code_execution_platform.dto;


import java.util.UUID;

public class SubmissionResponse {

    private UUID submissionId;
    private String status;

    public SubmissionResponse(UUID submissionId, String status) {
        this.submissionId = submissionId;
        this.status = status;
    }

    public UUID getSubmissionId() {
        return submissionId;
    }

    public String getStatus() {
        return status;
    }
}

