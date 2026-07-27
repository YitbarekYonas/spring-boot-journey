package com.JavaBackEnd.spring_boot_journey_week5_day6.dto;

public class RefreshTokenResponse {

    private final String accessToken;
    private final String tokenType = "Bearer";
    private final long expiresIn;

    public RefreshTokenResponse(String accessToken, long expiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() { return accessToken; }
    public String getTokenType() { return tokenType; }
    public long getExpiresIn() { return expiresIn; }
}
