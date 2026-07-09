package com.JavaBackEnd.spring_boot_journey_week3_day2.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_users_email",
            columnNames = {"email"}
        )
    }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    protected User() {}

    public User(String name, String email, UserRole role) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    // ── Getters ──────────────────────────────────────────────────

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "User{id=" + id
               + ", name='" + name + "'"
               + ", email='" + email + "'"
               + ", role=" + role + "}";
    }
}