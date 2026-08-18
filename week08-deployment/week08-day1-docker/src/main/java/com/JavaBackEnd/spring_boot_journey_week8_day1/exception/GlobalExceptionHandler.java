package com.JavaBackEnd.spring_boot_journey_week8_day1.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex,
                                                          HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(build(
                HttpStatus.NOT_FOUND, ex.getMessage(), request, null));
    }

    @ExceptionHandler(InvalidTaskStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidState(InvalidTaskStateException ex,
                                                              HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(build(
                HttpStatus.CONFLICT, ex.getMessage(), request, null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                            HttpServletRequest request) {
        List<String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(build(
                HttpStatus.BAD_REQUEST, "Validation failed", request, fieldErrors));
    }

    private ErrorResponse build(HttpStatus status, String message,
                                 HttpServletRequest request, List<String> fieldErrors) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .message(message)
                .path(request.getRequestURI())
                .fieldErrors(fieldErrors)
                .build();
    }
}
