package com.JavaBackEnd.spring_boot_journey_week8_day1.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
