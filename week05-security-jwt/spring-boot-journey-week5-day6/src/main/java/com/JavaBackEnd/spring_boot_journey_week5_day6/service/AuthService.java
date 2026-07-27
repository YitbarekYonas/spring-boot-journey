package com.JavaBackEnd.spring_boot_journey_week5_day6.service;

import com.JavaBackEnd.spring_boot_journey_week5_day6.dto.LoginRequest;
import com.JavaBackEnd.spring_boot_journey_week5_day6.dto.LoginResponse;
import com.JavaBackEnd.spring_boot_journey_week5_day6.dto.RefreshTokenRequest;
import com.JavaBackEnd.spring_boot_journey_week5_day6.dto.RefreshTokenResponse;
import com.JavaBackEnd.spring_boot_journey_week5_day6.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    LoginResponse login(LoginRequest request, HttpServletRequest httpRequest);

    RefreshTokenResponse refreshAccessToken(RefreshTokenRequest request);

    void logout(String refreshTokenValue, User currentUser);

    void logoutAll(User currentUser);
}