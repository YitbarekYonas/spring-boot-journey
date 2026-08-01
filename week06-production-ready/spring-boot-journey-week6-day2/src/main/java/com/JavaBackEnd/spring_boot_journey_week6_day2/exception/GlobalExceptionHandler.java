package com.JavaBackEnd.spring_boot_journey_week6_day2.exception;

import com.JavaBackEnd.spring_boot_journey_week6_day2.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 1. Domain exceptions - custom hierarchy
    @ExceptionHandler(LibraryException.class)
    public ResponseEntity<ErrorResponse> handleLibraryException(
            LibraryException ex, HttpServletRequest request) {

        log.warn("Library exception [{}]: {}",
            ex.getErrorCode(), ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
            ex.getStatus(),
            ex.getErrorCode(),
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    // 2. Validation exceptions - @Valid failures
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

    // 3. Type mismatch - e.g., "abc" where Long expected
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String message = String.format(
            "Parameter '%s' has invalid value '%s'. Expected type: %s",
            ex.getName(),
            ex.getValue(),
            ex.getRequiredType() != null
                ? ex.getRequiredType().getSimpleName()
                : "unknown"
        );

        log.warn("Type mismatch: {}", message);

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST,
            "TYPE_MISMATCH",
            message,
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 4. Missing required parameters
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex, HttpServletRequest request) {

        String message = String.format(
            "Required parameter '%s' of type '%s' is missing",
            ex.getParameterName(),
            ex.getParameterType()
        );

        log.warn("Missing required parameter: {}", ex.getParameterName());

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.BAD_REQUEST,
            "MISSING_PARAMETER",
            message,
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 5. Database constraint violations
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        log.error("Data integrity violation: {}",
            ex.getMostSpecificCause().getMessage());

        ErrorResponse error = ErrorResponse.of(
            HttpStatus.CONFLICT,
            "DATA_INTEGRITY_VIOLATION",
            "The request conflicts with existing data. A unique constraint was violated.",
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // 6. Catch-all - unexpected exceptions
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