package com.code_execution_platform.dto;


import jakarta.validation.constraints.NotBlank;

public class SubmissionRequest {

    @NotBlank
    private String language;

    @NotBlank
    private String sourceCode;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }
}

