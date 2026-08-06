package com.JavaBackEnd.spring_boot_journey_week6_day4.dto.response;

import com.JavaBackEnd.spring_boot_journey_week6_day4.entity.User;
import com.JavaBackEnd.spring_boot_journey_week6_day4.entity.UserRole;
import java.time.LocalDateTime;

public class UserResponse {

    private final Long id;
    private final String name;
    private final String email;
    private final UserRole role;
    private final boolean enabled;
    private final LocalDateTime createdAt;

    public static UserResponse from(User user) {
        return new UserResponse(user);
    }

    private UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.enabled = user.isEnabled();
        this.createdAt = user.getCreatedAt();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
    public boolean isEnabled() { return enabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}