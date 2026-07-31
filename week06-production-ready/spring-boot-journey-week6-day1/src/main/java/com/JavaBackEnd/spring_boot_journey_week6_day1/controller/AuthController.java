package com.JavaBackEnd.spring_boot_journey_week6_day1.controller;

import com.JavaBackEnd.spring_boot_journey_week6_day1.dto.LoginRequest;
import com.JavaBackEnd.spring_boot_journey_week6_day1.dto.LoginResponse;
import com.JavaBackEnd.spring_boot_journey_week6_day1.dto.RegisterRequest;
import com.JavaBackEnd.spring_boot_journey_week6_day1.entity.User;
import com.JavaBackEnd.spring_boot_journey_week6_day1.security.CustomUserDetails;
import com.JavaBackEnd.spring_boot_journey_week6_day1.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.login(request, httpRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        return ResponseEntity.ok(authService.refreshAccessToken(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        authService.logout(request.get("refreshToken"), currentUser.getUser());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Map<String, String>> logoutAll(
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        authService.logoutAll(currentUser.getUser());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out from all devices");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        Map<String, Object> response = new HashMap<>();
        response.put("userId", currentUser.getUserId());
        response.put("email", currentUser.getUsername());
        response.put("name", currentUser.getUser().getName());
        response.put("role", currentUser.getUser().getRole());
        return ResponseEntity.ok(response);
    }
}