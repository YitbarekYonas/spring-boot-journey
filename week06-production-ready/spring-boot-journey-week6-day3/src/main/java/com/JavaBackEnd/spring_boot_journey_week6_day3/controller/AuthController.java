package com.JavaBackEnd.spring_boot_journey_week6_day3.controller;

import com.JavaBackEnd.spring_boot_journey_week6_day3.dto.ChangePasswordRequest;
import com.JavaBackEnd.spring_boot_journey_week6_day3.dto.LoginRequest;
import com.JavaBackEnd.spring_boot_journey_week6_day3.dto.RegisterRequest;
import com.JavaBackEnd.spring_boot_journey_week6_day3.entity.User;
import com.JavaBackEnd.spring_boot_journey_week6_day3.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        // For demo, using hardcoded userId = 1
        authService.changePassword(1L, request);
        return ResponseEntity.ok("Password changed successfully");
    }
}