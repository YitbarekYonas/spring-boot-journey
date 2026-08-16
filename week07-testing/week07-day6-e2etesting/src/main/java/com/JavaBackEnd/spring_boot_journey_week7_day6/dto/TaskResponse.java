package com.JavaBackEnd.spring_boot_journey_week7_day6.dto;

import com.JavaBackEnd.spring_boot_journey_week7_day6.model.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Response DTO — what goes OUT to the client. Never the entity itself
// (General Best Practice #2 / Week 6, Day 4) — keeps internal persistence
// structure decoupled from the public API contract.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private Task.Status status;
    private Task.Priority priority;
    private LocalDate dueDate;
    private String ownerEmail;
    private LocalDateTime createdAt;

    public static TaskResponse fromEntity(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .ownerEmail(task.getOwnerEmail())
                .createdAt(task.getCreatedAt())
                .build();
    }
}
