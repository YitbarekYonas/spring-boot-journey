package com.JavaBackEnd.spring_boot_journey_week7_day6.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
