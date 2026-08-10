package com.JavaBackEnd.spring_boot_journey_week6_day7.dto.request;

import com.JavaBackEnd.spring_boot_journey_week6_day7.entity.Task;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// All fields optional on update — only non-null fields get applied
@Getter
@NoArgsConstructor
public class UpdateTaskRequest {

    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 500)
    private String description;

    private Task.Status   status;
    private Task.Priority priority;

    @Future(message = "Due date must be in the future")
    private LocalDate dueDate;
}
