package com.code_execution_platform.configs;

import com.code_execution_platform.exceptions.ExecutionException;
import com.code_execution_platform.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(error(ex.getMessage()));
    }

    @ExceptionHandler(ExecutionException.class)
    public ResponseEntity<?> handleExecution(ExecutionException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(error(ex.getMessage()));
    }

    private Map<String, Object> error(String message) {
        return Map.of(
                "timestamp", Instant.now(),
                "error", message
        );
    }
}

