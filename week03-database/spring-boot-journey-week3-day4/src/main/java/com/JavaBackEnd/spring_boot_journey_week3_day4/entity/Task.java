package com.JavaBackEnd.spring_boot_journey_week3_day4.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskPriority priority;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Many-to-One: Many Tasks belong to one User
    @ManyToOne(fetch = FetchType.LAZY)  // ALWAYS use LAZY
    @JoinColumn(
        name = "assigned_user_id",      // Foreign key column in tasks table
        nullable = true                 // Task can exist without assigned user
    )
    private User assignedUser;

    protected Task() {}

    public Task(String title, String description,
                TaskStatus status, TaskPriority priority,
                LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.status = status != null ? status : TaskStatus.TODO;
        this.priority = priority != null ? priority : TaskPriority.MEDIUM;
        this.dueDate = dueDate;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) {
        this.priority = priority;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public User getAssignedUser() { return assignedUser; }
    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
    }

    @Override
    public String toString() {
        return "Task{id=" + id + ", title='" + title + "', status=" + status + "}";
    }
}