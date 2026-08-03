package com.JavaBackEnd.spring_boot_journey_week6_day3.exception;

import org.springframework.http.HttpStatus;

public abstract class LibraryException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    protected LibraryException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() { return status; }
    public String getErrorCode() { return errorCode; }
}