package com.JavaBackEnd.spring_boot_journey_week5_day3.dto;

import com.JavaBackEnd.spring_boot_journey_week5_day3.entity.UserRole;

public class RegisterResponse {
    private Long id;
    private String name;
    private String email;
    private UserRole role;
    private String message;

    public RegisterResponse(Long id, String name, String email, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.message = "Registration successful";
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
    public String getMessage() { return message; }
}