package com.JavaBackEnd.spring_boot_journey_week7_day1.service;

import com.JavaBackEnd.spring_boot_journey_week7_day1.model.Task;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Validates a Task and returns a result object with all errors collected.
// Returning a result object (instead of throwing immediately) makes it
// easier to test multiple validation rules in one test.
public class TaskValidator {

    @Getter
    public static class ValidationResult {
        private final List<String> errors = new ArrayList<>();

        public void addError(String error) { errors.add(error); }
        public boolean isValid()           { return errors.isEmpty(); }
    }

    public ValidationResult validate(Task task) {
        ValidationResult result = new ValidationResult();

        if (task == null) {
            result.addError("Task cannot be null");
            return result;   // stop early — nothing else to check
        }

        // Title rules
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            result.addError("Title is required");
        } else if (task.getTitle().length() < 3) {
            result.addError("Title must be at least 3 characters");
        } else if (task.getTitle().length() > 100) {
            result.addError("Title cannot exceed 100 characters");
        }

        // Email rules
        if (task.getOwnerEmail() == null || task.getOwnerEmail().isBlank()) {
            result.addError("Owner email is required");
        } else if (!task.getOwnerEmail().contains("@")) {
            result.addError("Owner email must be valid");
        }

        // Date rule
        if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
            result.addError("Due date cannot be in the past");
        }

        // Priority and type must be set
        if (task.getPriority() == null) result.addError("Priority is required");
        if (task.getType()     == null) result.addError("Type is required");

        return result;
    }

    public boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;
        return email.contains("@") && email.contains(".");
    }

    public boolean isTitleValid(String title) {
        if (title == null || title.isBlank()) return false;
        int len = title.trim().length();
        return len >= 3 && len <= 100;
    }
}
