package com.JavaBackEnd.jwtauthservice.service;

import com.JavaBackEnd.jwtauthservice.dto.*;
import com.JavaBackEnd.jwtauthservice.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request, HttpServletRequest httpRequest);

    RefreshTokenResponse refreshAccessToken(RefreshTokenRequest request);

    void logout(String refreshTokenValue, User currentUser);

    void logoutAll(User currentUser);

    void changePassword(Long userId, ChangePasswordRequest request);
}