package com.code_execution_platform.controller;

import com.code_execution_platform.dto.SubmissionRequest;
import com.code_execution_platform.dto.SubmissionResponse;
import com.code_execution_platform.model.Submission;
import com.code_execution_platform.repository.SubmissionRepository;
import com.code_execution_platform.service.RateLimiterService;
import com.code_execution_platform.service.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final SubmissionRepository submissionRepository;
    private final RateLimiterService rateLimiterService;

    public SubmissionController(
            SubmissionService submissionService,
            SubmissionRepository submissionRepository,
            RateLimiterService rateLimiterService
    ) {
        this.submissionService = submissionService;
        this.submissionRepository = submissionRepository;
        this.rateLimiterService = rateLimiterService;
    }

    @PostMapping
    public SubmissionResponse submit(@Valid @RequestBody SubmissionRequest request) {

        // For now, single user
        String userId = "anonymous";

        if (!rateLimiterService.isAllowed(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Submission rate limit exceeded. Try again later."
            );
        }

        UUID submissionId =
                submissionService.submit(
                        request.getLanguage(),
                        request.getSourceCode()
                );

        return new SubmissionResponse(submissionId, "QUEUED");
    }

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
