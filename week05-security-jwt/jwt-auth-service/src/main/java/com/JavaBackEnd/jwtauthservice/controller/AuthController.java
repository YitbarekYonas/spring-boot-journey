package com.JavaBackEnd.jwtauthservice.controller;

import com.JavaBackEnd.jwtauthservice.dto.*;
import com.JavaBackEnd.jwtauthservice.security.CustomUserDetails;
import com.JavaBackEnd.jwtauthservice.service.AuthService;
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
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
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
    public ResponseEntity<RefreshTokenResponse> refresh(
            @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshAccessToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestBody RefreshTokenRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        authService.logout(request.getRefreshToken(), currentUser.getUser());

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

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        authService.changePassword(currentUser.getUserId(), request);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password changed successfully. "
            + "Please log in again on all devices.");
        return ResponseEntity.ok(response);
    }
}