package com.code_execution_platform.controller;

import com.code_execution_platform.dto.SubmissionRequest;
import com.code_execution_platform.dto.SubmissionResponse;
import com.code_execution_platform.model.Submission;
import com.code_execution_platform.repository.SubmissionRepository;
import com.code_execution_platform.service.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final SubmissionRepository submissionRepository;

    public SubmissionController(
            SubmissionService submissionService,
            SubmissionRepository submissionRepository
    ) {
        this.submissionService = submissionService;
        this.submissionRepository = submissionRepository;
    }

    /**
     * Async submission endpoint
     * Returns immediately with QUEUED status
     */
    @PostMapping
    public SubmissionResponse submit(@Valid @RequestBody SubmissionRequest request) {

        UUID submissionId =
                submissionService.submit(
                        request.getLanguage(),
                        request.getSourceCode()
                );

        return new SubmissionResponse(submissionId, "QUEUED");
    }

    /**
     * Check submission status (important for async systems)
     */
    @GetMapping("/{submissionId}/status")
    public SubmissionResponse getStatus(@PathVariable UUID submissionId) {

        Submission submission =
                submissionRepository.findById(submissionId)
                        .orElseThrow(() ->
                                new RuntimeException("Submission not found")
                        );

        return new SubmissionResponse(
                submission.getId(),
                submission.getStatus().name()
        );
    }
}


