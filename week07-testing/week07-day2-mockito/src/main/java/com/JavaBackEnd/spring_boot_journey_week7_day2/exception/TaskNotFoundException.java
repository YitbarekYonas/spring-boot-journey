package com.JavaBackEnd.spring_boot_journey_week7_day2.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long id) {
        super("Task not found: id=" + id);
    }
}
