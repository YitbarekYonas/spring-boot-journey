package com.JavaBackEnd.spring_boot_journey_week6_day3.exception;

import com.JavaBackEnd.spring_boot_journey_week6_day3.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(LibraryException.class)
    public ResponseEntity<ErrorResponse> handleLibraryException(
            LibraryException ex, HttpServletRequest request) {

        log.warn("Library exception [{}]: {}", ex.getErrorCode(), ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
            ex.getStatus(),
            ex.getErrorCode(),
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> new ErrorResponse.FieldError(
                    fe.getField(),
                    fe.getRejectedValue(),
                    fe.getDefaultMessage()
                ))
                .toList();

        log.warn("Validation failed on {} field(s)", fieldErrors.size());

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_FAILED",
            "Request validation failed. Check 'fieldErrors' for details.",
            request.getRequestURI()
        ).withFieldErrors(fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {

        List<ErrorResponse.FieldError> fieldErrors = ex.getConstraintViolations()
                .stream()
                .map(cv -> new ErrorResponse.FieldError(
                    cv.getPropertyPath().toString().replaceFirst(".*\\.", ""),
                    cv.getInvalidValue(),
                    cv.getMessage()
                ))
                .toList();

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_FAILED",
            "Request parameter validation failed.",
            request.getRequestURI()
        ).withFieldErrors(fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {

        log.error("Unexpected error at {}: {}", request.getRequestURI(),
            ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_ERROR",
            "An unexpected error occurred. Please try again.",
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}