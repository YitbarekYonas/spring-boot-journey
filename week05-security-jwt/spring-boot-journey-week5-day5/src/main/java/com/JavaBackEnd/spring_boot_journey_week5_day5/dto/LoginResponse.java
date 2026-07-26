package com.JavaBackEnd.spring_boot_journey_week5_day5.dto;

import com.JavaBackEnd.spring_boot_journey_week5_day5.entity.UserRole;

public class LoginResponse {

    private final String token;
    private final String tokenType = "Bearer";
    private final String email;
    private final String name;
    private final UserRole role;
    private final long expiresIn;

    public LoginResponse(String token, String email, String name,
                         UserRole role, long expiresIn) {
        this.token = token;
        this.email = email;
        this.name = name;
        this.role = role;
        this.expiresIn = expiresIn;
    }

    public String getToken() { return token; }
    public String getTokenType() { return tokenType; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public UserRole getRole() { return role; }
    public long getExpiresIn() { return expiresIn; }
}