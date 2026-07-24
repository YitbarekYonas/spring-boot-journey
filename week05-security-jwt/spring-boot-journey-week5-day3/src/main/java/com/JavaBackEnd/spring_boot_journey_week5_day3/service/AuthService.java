package com.JavaBackEnd.spring_boot_journey_week5_day3.service;

import com.JavaBackEnd.spring_boot_journey_week5_day3.dto.*;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    void changePassword(Long userId, ChangePasswordRequest request);
}