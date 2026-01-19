package com.code_execution_platform.controller;

import com.code_execution_platform.exceptions.ResourceNotFoundException;
import com.code_execution_platform.model.ExecutionResult;
import com.code_execution_platform.repository.ExecutionResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
@RequiredArgsConstructor
@RestController
@RequestMapping("/results")
public class ResultController {

    private final ExecutionResultRepository repository;


    @GetMapping("/{submissionId}")
    public ExecutionResult getResult(@PathVariable UUID submissionId) {
        return repository.findBySubmissionId(submissionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Result not found for submission " + submissionId)
                );
    }

}