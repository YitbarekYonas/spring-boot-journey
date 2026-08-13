package com.JavaBackEnd.spring_boot_journey_week7_day3.dto.response;

import com.JavaBackEnd.spring_boot_journey_week7_day3.model.Task;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class TaskResponse {
    private final Long          id;
    private final String        title;
    private final String        description;
    private final Task.Status   status;
    private final Task.Priority priority;
    private final LocalDate     dueDate;
    private final String        ownerEmail;
    private final LocalDateTime createdAt;

    public static TaskResponse from(Task task) {
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
