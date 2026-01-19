package com.code_execution_platform.service;

import com.code_execution_platform.exceptions.ExecutionException;
import com.code_execution_platform.model.ExecutionResult;
import com.code_execution_platform.model.enums.ExecutionVerdict;
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
        ExecutionResult result = new ExecutionResult();
        result.setSubmissionId(submissionId);

        long startTime = System.currentTimeMillis();

        try {
            tempDir = Files.createTempDirectory("docker-exec-");
            Path codeFile = tempDir.resolve("code.py");
            Files.writeString(codeFile, sourceCode);

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

            Process process = new ProcessBuilder(command).start();

            boolean finished =
                    process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                result.setVerdict(ExecutionVerdict.TIME_LIMIT_EXCEEDED);
                result.setExitCode(-1);
                return result;
            }

            int exitCode = process.exitValue();
            result.setExitCode(exitCode);
            result.setStdout(read(process.getInputStream()));
            result.setStderr(read(process.getErrorStream()));

            if (exitCode == 0) {
                result.setVerdict(ExecutionVerdict.SUCCESS);
            } else {
                result.setVerdict(ExecutionVerdict.RUNTIME_ERROR);
            }

        } catch (Exception e) {
            result.setVerdict(ExecutionVerdict.SYSTEM_ERROR);
            throw new ExecutionException("Docker execution failed", e);
        } finally {
            cleanup(tempDir);
        }

        result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        return result;
    }

    private String read(InputStream is) throws Exception {
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    private void cleanup(Path dir) {
        try {
            if (dir != null) {
                Files.walk(dir)
                        .sorted((a, b) -> b.compareTo(a))
                        .forEach(p -> {
                            try { Files.deleteIfExists(p); } catch (Exception ignored) {}
                        });
            }
        } catch (Exception ignored) {}
    }
}
