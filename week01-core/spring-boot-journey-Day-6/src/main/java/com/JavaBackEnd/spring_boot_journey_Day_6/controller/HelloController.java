package com.JavaBackEnd.spring_boot_journey_Day_6.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HelloController {
    
    @GetMapping("/hello")
    public Map<String, Object> sayHello() {
        return Map.of(
            "message", "Hello from Spring Boot!",
            "timestamp", LocalDateTime.now(),
            "custom", "This uses the custom ObjectMapper"
        );
    }
}