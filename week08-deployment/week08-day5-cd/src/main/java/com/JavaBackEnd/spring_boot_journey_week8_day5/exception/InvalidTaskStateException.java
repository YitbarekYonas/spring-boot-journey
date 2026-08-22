package com.JavaBackEnd.spring_boot_journey_week8_day5.exception;

public class InvalidTaskStateException extends RuntimeException {
    public InvalidTaskStateException(String message) {
        super(message);
    }
}
