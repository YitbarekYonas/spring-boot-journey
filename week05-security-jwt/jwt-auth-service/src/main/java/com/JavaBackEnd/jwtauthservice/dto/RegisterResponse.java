package com.JavaBackEnd.jwtauthservice.dto;

import com.JavaBackEnd.jwtauthservice.entity.UserRole;

public class RegisterResponse {
    private final Long id;
    private final String name;
    private final String email;
    private final UserRole role;

    public RegisterResponse(Long id, String name, String email, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
}