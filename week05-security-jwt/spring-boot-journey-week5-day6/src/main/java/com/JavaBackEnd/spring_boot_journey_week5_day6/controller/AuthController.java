package com.JavaBackEnd.spring_boot_journey_week5_day6.controller;

import com.JavaBackEnd.spring_boot_journey_week5_day6.dto.LoginRequest;
import com.JavaBackEnd.spring_boot_journey_week5_day6.dto.LoginResponse;
import com.JavaBackEnd.spring_boot_journey_week5_day6.dto.RefreshTokenRequest;
import com.JavaBackEnd.spring_boot_journey_week5_day6.dto.RefreshTokenResponse;
import com.JavaBackEnd.spring_boot_journey_week5_day6.security.CustomUserDetails;
import com.JavaBackEnd.spring_boot_journey_week5_day6.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        LoginResponse response = authService.login(request, httpRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(
            @RequestBody RefreshTokenRequest request) {

        RefreshTokenResponse response = authService.refreshAccessToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestBody RefreshTokenRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        authService.logout(request.getRefreshToken(), currentUser.getUser());
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/logout-all")
    public ResponseEntity<String> logoutAll(
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        authService.logoutAll(currentUser.getUser());
        return ResponseEntity.ok("Logged out from all devices");
    }
}