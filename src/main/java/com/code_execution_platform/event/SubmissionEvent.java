package com.code_execution_platform.event;


import java.util.UUID;

public class SubmissionEvent {

    private UUID submissionId;
    private String language;

    public SubmissionEvent() {
    }

    public SubmissionEvent(UUID submissionId, String language) {
        this.submissionId = submissionId;
        this.language = language;
    }

    public UUID getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(UUID submissionId) {
        this.submissionId = submissionId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}

