package com.JavaBackEnd.greeting.controller;

import com.JavaBackEnd.greeting.service.GreetingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/greeting")
public class GreetingController {

    private final GreetingService greetingService;

    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GetMapping
    public String greet() {
        return greetingService.buildGreeting();
    }

    @GetMapping("/info")
    public String info() {
        return greetingService.getStrategyInfo();
    }
}
