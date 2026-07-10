package com.JavaBackEnd.spring_boot_journey_week3_day4.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // One-to-Many: User has many Tasks
    @OneToMany(
        mappedBy = "assignedUser",          // Field name in Task
        fetch = FetchType.LAZY,
        cascade = {CascadeType.PERSIST, CascadeType.MERGE}
        // NOT CascadeType.REMOVE - tasks should survive user deletion
    )
    private List<Task> tasks = new ArrayList<>();

    protected User() {}

    public User(String name, String email, UserRole role) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    // Helper methods to keep both sides in sync
    public void addTask(Task task) {
        tasks.add(task);
        task.setAssignedUser(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setAssignedUser(null);
    }

    // Getters and Setters
    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}