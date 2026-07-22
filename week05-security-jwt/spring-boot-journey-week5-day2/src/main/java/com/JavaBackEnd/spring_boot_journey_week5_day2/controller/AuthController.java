package com.JavaBackEnd.spring_boot_journey_week5_day2.controller;

import com.JavaBackEnd.spring_boot_journey_week5_day2.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Map.of("authenticated", false);
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return Map.of(
            "authenticated", true,
            "email", userDetails.getUsername(),
            "name", userDetails.getUser().getName(),
            "role", userDetails.getUser().getRole().name(),
            "userId", userDetails.getUserId()
        );
    }
}