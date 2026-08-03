package com.JavaBackEnd.spring_boot_journey_week6_day3.service;

import com.JavaBackEnd.spring_boot_journey_week6_day3.dto.ChangePasswordRequest;
import com.JavaBackEnd.spring_boot_journey_week6_day3.dto.LoginRequest;
import com.JavaBackEnd.spring_boot_journey_week6_day3.dto.RegisterRequest;
import com.JavaBackEnd.spring_boot_journey_week6_day3.entity.User;

public interface AuthService {

    User register(RegisterRequest request);

    User login(LoginRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);
}