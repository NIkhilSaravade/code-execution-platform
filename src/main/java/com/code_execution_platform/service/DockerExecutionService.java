package com.code_execution_platform.service;


import com.code_execution_platform.exceptions.ExecutionException;
import com.code_execution_platform.model.ExecutionResult;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class DockerExecutionService {

    private static final long TIMEOUT_SECONDS = 5;

    public ExecutionResult executePythonInDocker(String sourceCode, UUID submissionId) {

        Path tempDir = null;
        Path codeFile = null;

        ExecutionResult result = new ExecutionResult();
        result.setSubmissionId(submissionId);

        long start = System.currentTimeMillis();

        try {
            // 1. Create isolated temp directory
            tempDir = Files.createTempDirectory("docker-run-");
            codeFile = tempDir.resolve("code.py");
            Files.writeString(codeFile, sourceCode);

            // 2. Build docker command
            List<String> command = List.of(
                    "docker", "run", "--rm",
                    "--cpus=0.5",
                    "--memory=128m",
                    "--pids-limit=64",
                    "--network=none",
                    "--read-only",
                    "-v", tempDir.toAbsolutePath() + ":/app:ro",
                    "python-runner:1.0",
                    "/app/code.py"
            );

            ProcessBuilder pb = new ProcessBuilder(command);
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
            throw new ExecutionException("Docker execution failed: " + e.getMessage());
        } finally {
            cleanup(tempDir);
        }

        result.setExecutionTimeMs(System.currentTimeMillis() - start);
        return result;
    }

    private String read(InputStream is) throws Exception {
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    private void cleanup(Path dir) {
        try {
            if (dir != null)
                Files.walk(dir)
                        .sorted((a, b) -> b.compareTo(a))
                        .forEach(p -> {
                            try { Files.deleteIfExists(p); } catch (Exception ignored) {}
                        });
        } catch (Exception ignored) {}
    }
}

