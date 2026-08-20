package com.JavaBackEnd.spring_boot_journey_week8_day3.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
