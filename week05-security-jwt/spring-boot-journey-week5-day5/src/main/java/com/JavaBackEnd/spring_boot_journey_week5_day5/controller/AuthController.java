package com.JavaBackEnd.spring_boot_journey_week5_day5.controller;

import com.JavaBackEnd.spring_boot_journey_week5_day5.dto.LoginRequest;
import com.JavaBackEnd.spring_boot_journey_week5_day5.dto.LoginResponse;
import com.JavaBackEnd.spring_boot_journey_week5_day5.security.CustomUserDetails;
import com.JavaBackEnd.spring_boot_journey_week5_day5.security.CustomUserDetailsService;
import com.JavaBackEnd.spring_boot_journey_week5_day5.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          CustomUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        CustomUserDetails userDetails =
            (CustomUserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        LoginResponse response = new LoginResponse(
            token,
            userDetails.getUsername(),
            userDetails.getUser().getName(),
            userDetails.getUser().getRole(),
            7200000L
        );

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