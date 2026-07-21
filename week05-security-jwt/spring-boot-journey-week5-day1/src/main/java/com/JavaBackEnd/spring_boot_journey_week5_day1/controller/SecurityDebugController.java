package com.JavaBackEnd.spring_boot_journey_week5_day1.controller;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/debug")
public class SecurityDebugController {

    private final SecurityFilterChain securityFilterChain;

    public SecurityDebugController(SecurityFilterChain securityFilterChain) {
        this.securityFilterChain = securityFilterChain;
    }

    @GetMapping("/filters")
    public List<String> getRegisteredFilters() {
        return securityFilterChain.getFilters()
                .stream()
                .map(f -> f.getClass().getSimpleName())
                .collect(Collectors.toList());
    }
}