package com.JavaBackEnd.spring_boot_journey_week6_day1.service.impl;

import com.JavaBackEnd.spring_boot_journey_week6_day1.entity.RefreshToken;
import com.JavaBackEnd.spring_boot_journey_week6_day1.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface RefreshTokenServiceImpl {

    RefreshToken createRefreshToken(User user, HttpServletRequest request);

    RefreshToken validateRefreshToken(String tokenValue);

    void revokeRefreshToken(String tokenValue);

    void revokeAllUserTokens(User user);

    int cleanupExpiredTokens();
}