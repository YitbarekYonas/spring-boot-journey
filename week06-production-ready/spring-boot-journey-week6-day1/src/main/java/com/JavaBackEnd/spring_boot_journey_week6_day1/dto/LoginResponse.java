package com.JavaBackEnd.spring_boot_journey_week6_day1.dto;

import com.JavaBackEnd.spring_boot_journey_week6_day1.entity.UserRole;

public class LoginResponse {
    private final String accessToken;
    private final String refreshToken;
    private final String tokenType = "Bearer";
    private final String email;
    private final String name;
    private final UserRole role;
    private final long expiresIn;

    public LoginResponse(String accessToken, String refreshToken,
                         String email, String name, UserRole role,
                         long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.email = email;
        this.name = name;
        this.role = role;
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getTokenType() { return tokenType; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public UserRole getRole() { return role; }
    public long getExpiresIn() { return expiresIn; }
}