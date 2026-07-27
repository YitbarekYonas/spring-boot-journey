package com.JavaBackEnd.spring_boot_journey_week5_day6.service;

import com.JavaBackEnd.spring_boot_journey_week5_day6.entity.RefreshToken;
import com.JavaBackEnd.spring_boot_journey_week5_day6.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user, HttpServletRequest request);

    RefreshToken validateRefreshToken(String tokenValue);

    void revokeRefreshToken(String tokenValue);

    void revokeAllUserTokens(User user);

    int cleanupExpiredTokens();
}