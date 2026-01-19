package com.code_execution_platform.controller;

import com.code_execution_platform.dto.SubmissionRequest;
import com.code_execution_platform.dto.SubmissionResponse;
import com.code_execution_platform.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;
@RequiredArgsConstructor
@RestController
@RequestMapping("/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;



    @PostMapping
    public SubmissionResponse submit(@Valid @RequestBody SubmissionRequest request) {

        UUID submissionId =
                submissionService.submit(request.getLanguage(), request.getSourceCode());

        return new SubmissionResponse(submissionId, "COMPLETED");
    }
}

