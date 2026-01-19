package com.code_execution_platform.service;

import com.code_execution_platform.exceptions.ExecutionException;
import com.code_execution_platform.model.ExecutionResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
@Service
public class ExecutionService {
    /**
     * Phase 1 legacy local execution service.
     * Replaced by DockerExecutionService in Phase 2.
     */

    private static final long TIMEOUT_SECONDS = 5;
    private static final int MAX_SOURCE_SIZE = 10_000;

    public ExecutionResult executePython(String sourceCode, UUID submissionId) {

        if (sourceCode.length() > MAX_SOURCE_SIZE) {
            throw new ExecutionException("Source code size exceeds limit");
        }

        ExecutionResult result = new ExecutionResult();
        result.setSubmissionId(submissionId);

        long start = System.currentTimeMillis();
        Path tempFile = null;

        try {
            tempFile = Files.createTempFile("submission-", ".py");
            Files.writeString(tempFile, sourceCode);

            ProcessBuilder pb = new ProcessBuilder(
                    "python", tempFile.toAbsolutePath().toString()
            );

            Process process = pb.start();
            boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                throw new ExecutionException("Time Limit Exceeded");
            }

            result.setExitCode(process.exitValue());
            result.setStdout(read(process.getInputStream()));
            result.setStderr(read(process.getErrorStream()));

        } catch (ExecutionException e) {
            throw e;
        } catch (Exception e) {
            throw new ExecutionException("Execution failed: " + e.getMessage());
        } finally {
            deleteTempFile(tempFile);
        }

        result.setExecutionTimeMs(System.currentTimeMillis() - start);
        return result;
    }

    private String read(InputStream is) throws IOException {
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    private void deleteTempFile(Path path) {
        try {
            if (path != null) Files.deleteIfExists(path);
        } catch (IOException ignored) {}
    }
}
