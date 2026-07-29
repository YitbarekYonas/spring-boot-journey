package com.JavaBackEnd.jwtauthservice.service;

import com.JavaBackEnd.jwtauthservice.entity.RefreshToken;
import com.JavaBackEnd.jwtauthservice.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user, HttpServletRequest request);

    RefreshToken validateRefreshToken(String tokenValue);

    void revokeRefreshToken(String tokenValue);

    void revokeAllUserTokens(User user);

    int cleanupExpiredTokens();
}