package com.JavaBackEnd.spring_boot_journey_week6_day1.service;

import com.JavaBackEnd.spring_boot_journey_week6_day1.dto.LoginRequest;
import com.JavaBackEnd.spring_boot_journey_week6_day1.dto.LoginResponse;
import com.JavaBackEnd.spring_boot_journey_week6_day1.dto.RegisterRequest;
import com.JavaBackEnd.spring_boot_journey_week6_day1.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    User register(RegisterRequest request);

    LoginResponse login(LoginRequest request, HttpServletRequest httpRequest);

    LoginResponse refreshAccessToken(String refreshToken);

    void logout(String refreshTokenValue, User currentUser);

    void logoutAll(User currentUser);
}