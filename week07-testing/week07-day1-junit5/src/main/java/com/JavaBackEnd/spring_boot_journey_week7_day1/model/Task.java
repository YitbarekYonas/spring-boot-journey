package com.JavaBackEnd.spring_boot_journey_week7_day1.model;

import lombok.*;

import java.time.LocalDate;

// ── Plain Java class — no Spring, no JPA ──────────────────────────────────
// Week 7 Day 1 lesson: unit tests test pure Java logic.
// No Spring context needed = tests run in milliseconds, not seconds.
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Task {

    public enum Priority { LOW, MEDIUM, HIGH }
    public enum Type     { BUG, FEATURE, DOCUMENTATION, REFACTOR }

    private Long     id;
    private String   title;
    private String   ownerEmail;
    private Priority priority;
    private Type     type;
    private LocalDate dueDate;
    private boolean  completed;

    public boolean isOverdue() {
        return dueDate != null
            && dueDate.isBefore(LocalDate.now())
            && !completed;
    }
}
