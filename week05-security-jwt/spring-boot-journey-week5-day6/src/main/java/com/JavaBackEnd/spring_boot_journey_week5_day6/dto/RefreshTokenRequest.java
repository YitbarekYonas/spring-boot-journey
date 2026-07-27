package com.JavaBackEnd.spring_boot_journey_week5_day6.dto;

public class RefreshTokenRequest {
    private String refreshToken;

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}