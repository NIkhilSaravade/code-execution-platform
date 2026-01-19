package com.code_execution_platform.service;

import com.code_execution_platform.exceptions.ExecutionException;
import com.code_execution_platform.model.ExecutionResult;
import com.code_execution_platform.model.Submission;
import com.code_execution_platform.model.SubmissionStatus;
import com.code_execution_platform.repository.ExecutionResultRepository;
import com.code_execution_platform.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final ExecutionResultRepository executionResultRepository;
    private final ExecutionService executionService;

    public SubmissionService(
            SubmissionRepository submissionRepository,
            ExecutionResultRepository executionResultRepository,
            ExecutionService executionService
    ) {
        this.submissionRepository = submissionRepository;
        this.executionResultRepository = executionResultRepository;
        this.executionService = executionService;
    }

    public UUID submit(String language, String sourceCode) {

        Submission submission = new Submission();
        submission.setLanguage(language);
        submission.setSourceCode(sourceCode);
        submission.setStatus(SubmissionStatus.RUNNING);

        submissionRepository.save(submission);

        try {
            ExecutionResult result =
                    executionService.executePython(sourceCode, submission.getId());

            executionResultRepository.save(result);
            submission.setStatus(SubmissionStatus.COMPLETED);

        } catch (ExecutionException ex) {
            submission.setStatus(SubmissionStatus.FAILED);
            throw ex;
        } finally {
            submissionRepository.save(submission);
        }

        return submission.getId();
    }
}
