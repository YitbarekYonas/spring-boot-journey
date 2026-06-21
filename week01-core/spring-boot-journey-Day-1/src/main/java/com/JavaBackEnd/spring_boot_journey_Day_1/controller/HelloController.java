package com.JavaBackEnd.spring_boot_journey_Day_1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/hello")
public class HelloController {
    
    @GetMapping
    public Map<String, String> sayHello() {
        return Map.of(
            "message", "Hello from Spring Boot!",
            "timestamp", String.valueOf(System.currentTimeMillis())
        );
    }
}