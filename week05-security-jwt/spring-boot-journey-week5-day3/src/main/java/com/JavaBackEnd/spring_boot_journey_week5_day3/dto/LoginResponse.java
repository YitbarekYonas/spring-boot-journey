package com.JavaBackEnd.spring_boot_journey_week5_day3.dto;

import com.JavaBackEnd.spring_boot_journey_week5_day3.entity.UserRole;

public class LoginResponse {
    private String email;
    private UserRole role;
    private String message;

    public LoginResponse(String email, UserRole role) {
        this.email = email;
        this.role = role;
        this.message = "Login successful";
    }

    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
    public String getMessage() { return message; }
}