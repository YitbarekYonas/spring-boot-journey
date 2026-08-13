package com.JavaBackEnd.spring_boot_journey_week7_day3.model;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Task {

    public enum Status   { TODO, IN_PROGRESS, DONE }
    public enum Priority { LOW, MEDIUM, HIGH }

    private Long      id;
    private String    title;
    private String    description;
    private Status    status;
    private Priority  priority;
    private LocalDate dueDate;
    private String    ownerEmail;
    private LocalDateTime createdAt;
}
