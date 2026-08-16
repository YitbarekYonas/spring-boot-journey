package com.JavaBackEnd.spring_boot_journey_week7_day6.dto;

import com.JavaBackEnd.spring_boot_journey_week7_day6.model.Task;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// Request DTO — what comes IN from the client. Kept separate from the
// Response DTO (Week 6, Day 4) since input and output shapes serve
// different purposes (e.g. clients never send `id` or `createdAt`).
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Status is required")
    private Task.Status status;

    @NotNull(message = "Priority is required")
    private Task.Priority priority;

    private LocalDate dueDate;

    @NotBlank(message = "Owner email is required")
    @Email(message = "Owner email must be a valid email address")
    private String ownerEmail;
}
