package com.JavaBackEnd.spring_boot_journey_week5_day4.controller;

import com.JavaBackEnd.spring_boot_journey_week5_day4.security.CustomUserDetails;
import com.JavaBackEnd.spring_boot_journey_week5_day4.security.CustomUserDetailsService;
import com.JavaBackEnd.spring_boot_journey_week5_day4.security.JwtService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug/jwt")
public class JwtDebugController {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtDebugController(JwtService jwtService,
                              CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/generate/{email}")
    public Map<String, Object> generateTestToken(@PathVariable String email) {
        CustomUserDetails userDetails =
            (CustomUserDetails) userDetailsService.loadUserByUsername(email);

        String token = jwtService.generateToken(userDetails);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("email", jwtService.extractEmail(token));
        result.put("role", jwtService.extractRole(token));
        result.put("userId", jwtService.extractUserId(token));
        result.put("issuedAt", jwtService.extractIssuedAt(token));
        result.put("expiration", jwtService.extractExpiration(token));
        result.put("isExpired", jwtService.isTokenExpired(token));

        return result;
    }
}