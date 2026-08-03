package com.JavaBackEnd.spring_boot_journey_week6_day3.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String errorCode;
    private final String message;
    private final String path;
    private List<FieldError> fieldErrors;

    private ErrorResponse(HttpStatus status, String errorCode,
                          String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.errorCode = errorCode;
        this.message = message;
        this.path = path;
    }

    public static ErrorResponse of(HttpStatus status, String errorCode,
                                   String message, String path) {
        return new ErrorResponse(status, errorCode, message, path);
    }

    public ErrorResponse withFieldErrors(List<FieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
        return this;
    }

    public static class FieldError {
        private final String field;
        private final Object rejectedValue;
        private final String message;

        public FieldError(String field, Object rejectedValue, String message) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.message = message;
        }

        public String getField() { return field; }
        public Object getRejectedValue() { return rejectedValue; }
        public String getMessage() { return message; }
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getErrorCode() { return errorCode; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public List<FieldError> getFieldErrors() { return fieldErrors; }
}