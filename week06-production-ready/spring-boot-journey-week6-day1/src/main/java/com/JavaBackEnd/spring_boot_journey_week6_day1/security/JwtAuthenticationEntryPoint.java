package com.JavaBackEnd.spring_boot_journey_week6_day1.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String jwtError = (String) request.getAttribute("JWT_ERROR");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", 401);
        body.put("error", "Unauthorized");

        if ("Token expired".equals(jwtError)) {
            body.put("message",
                "Access token has expired. Use your refresh token at /api/auth/refresh to get a new one.");
            body.put("action", "REFRESH_TOKEN");
        } else if ("Invalid token".equals(jwtError)) {
            body.put("message",
                "Invalid access token. Please log in again.");
            body.put("action", "LOGIN_REQUIRED");
        } else {
            body.put("message",
                "Authentication required. Please provide a valid JWT token.");
            body.put("action", "LOGIN_REQUIRED");
        }

        body.put("path", request.getRequestURI());

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}