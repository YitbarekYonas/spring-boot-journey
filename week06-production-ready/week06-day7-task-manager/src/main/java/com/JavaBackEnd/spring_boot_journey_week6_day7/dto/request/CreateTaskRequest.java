package com.JavaBackEnd.spring_boot_journey_week6_day7.dto.request;

import com.JavaBackEnd.spring_boot_journey_week6_day7.entity.Task;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// ── CreateTaskRequest ──────────────────────────────────────────────────────
@Getter
@NoArgsConstructor
public class CreateTaskRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private Task.Priority priority = Task.Priority.MEDIUM;

    @Future(message = "Due date must be in the future")
    private LocalDate dueDate;

    @NotBlank(message = "Owner email is required")
    @Email(message = "Owner email must be valid")
    private String ownerEmail;
}
