package com.JavaBackEnd.spring_boot_journey_week7_day5.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

// ── Why Day 5 exists at all ─────────────────────────────────────────────────
// Day 4's @DataJpaTest proved the repository against H2 — fast, but H2 is
// NOT Postgres. Different dialects handle things differently: date/time
// functions, case sensitivity, enum storage, JSON columns, sequence vs
// identity generation, locking behavior. A query that passes on H2 can
// still break in production against real Postgres.
//
// Day 5 keeps this exact same entity, but Day 5's integration test runs it
// against a REAL PostgreSQL container via Testcontainers — the same engine
// production uses — so a green test here means something H2 alone can't
// promise.
@Entity
@Table(name = "tasks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
