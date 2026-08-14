package com.JavaBackEnd.spring_boot_journey_week7_day4.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

// ── Why this is a real @Entity now (Day 3 it was a plain POJO) ─────────────
// @WebMvcTest (Day 3) never touched a database, so Task was just a builder
// object handed back by a mocked service.
//
// @DataJpaTest (Day 4) boots a real (in-memory H2) persistence context, so
// Task now needs to actually be mapped to a table: @Entity, @Id,
// @GeneratedValue, @Column, @Enumerated, etc.
@Entity
@Table(name = "tasks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// NOTE: @Data is intentionally avoided on entities (Week 6, Day 5) —
// its generated equals()/hashCode()/toString() can misbehave with lazy
// associations. We use targeted Lombok annotations instead.
public class Task {

    public enum Status { TODO, IN_PROGRESS, DONE }
    public enum Priority { LOW, MEDIUM, HIGH }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Priority priority;

    private LocalDate dueDate;

    @Column(nullable = false)
    private String ownerEmail;

    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = Status.TODO;
        }
    }
}
