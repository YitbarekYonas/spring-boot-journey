package com.JavaBackEnd.spring_boot_journey_week6_day7.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

// ── Week 6 Day 2 concept: Global Exception Handling ───────────────────────
// One class catches ALL exceptions across ALL controllers.
// Every exception maps to a consistent JSON error shape.
// Controllers stay clean — no try-catch blocks needed.
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ── Consistent error response shape ───────────────────────────────────
    @Getter @Builder
    public static class ErrorResponse {
        private final int       status;
        private final String    error;
        private final String    message;
        private final LocalDateTime timestamp;
        private Map<String, String> fieldErrors; // only populated for validation failures
    }

    // ── Custom exceptions ──────────────────────────────────────────────────
    public static class TaskNotFoundException extends RuntimeException {
        public TaskNotFoundException(Long id) {
            super("Task not found: id=" + id);
        }
    }

    public static class UnauthorizedActionException extends RuntimeException {
        public UnauthorizedActionException(String msg) { super(msg); }
    }

    public static class DuplicateTaskException extends RuntimeException {
        public DuplicateTaskException(String msg) { super(msg); }
    }

    // ── Handlers ──────────────────────────────────────────────────────────

    // 404 — task doesn't exist
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(TaskNotFoundException ex) {
        log.warn("Task not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(error(404, "Not Found", ex.getMessage()));
    }

    // 403 — caller doesn't own the task
    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedActionException ex) {
        log.warn("Unauthorized action: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(error(403, "Forbidden", ex.getMessage()));
    }

    // 409 — business rule conflict
    @ExceptionHandler(DuplicateTaskException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateTaskException ex) {
        log.warn("Duplicate task: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(error(409, "Conflict", ex.getMessage()));
    }

    // 400 — @Valid annotation triggered a validation failure
    // Returns per-field errors so the client knows exactly what to fix
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        f -> f.getDefaultMessage() != null ? f.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing  // keep first error per field
                ));

        log.warn("Validation failed — {} field error(s): {}", fieldErrors.size(), fieldErrors);

        ErrorResponse body = ErrorResponse.builder()
                .status(400).error("Bad Request")
                .message("Validation failed — check fieldErrors")
                .timestamp(LocalDateTime.now())
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.badRequest().body(body);
    }

    // 400 — illegal argument (e.g., bad enum value)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArg(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(error(400, "Bad Request", ex.getMessage()));
    }

    // 500 — anything else that wasn't handled above
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error(500, "Internal Server Error", "An unexpected error occurred"));
    }

    // ── Helper ────────────────────────────────────────────────────────────
    private ErrorResponse error(int status, String error, String message) {
        return ErrorResponse.builder()
                .status(status).error(error)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
