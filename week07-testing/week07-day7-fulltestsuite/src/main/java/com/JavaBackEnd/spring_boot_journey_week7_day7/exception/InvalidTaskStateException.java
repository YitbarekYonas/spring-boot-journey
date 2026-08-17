package com.JavaBackEnd.spring_boot_journey_week7_day7.exception;

// A real business rule (not just CRUD) — gives the Mockito service tests
// something meaningful to verify beyond "does save() get called."
public class InvalidTaskStateException extends RuntimeException {
    public InvalidTaskStateException(String message) {
        super(message);
    }
}
