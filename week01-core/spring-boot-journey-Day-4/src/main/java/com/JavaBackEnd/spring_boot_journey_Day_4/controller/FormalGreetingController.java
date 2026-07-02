package com.JavaBackEnd.spring_boot_journey_Day_4.controller;

import com.JavaBackEnd.spring_boot_journey_Day_4.service.GreetingService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/formal")
public class FormalGreetingController {
    
    private final GreetingService greetingService;
    
    // @Qualifier tells Spring which bean to inject
    public FormalGreetingController(@Qualifier("formalGreetingService") GreetingService greetingService) {
        this.greetingService = greetingService;
    }
    
    @GetMapping
    public String getFormalGreeting() {
        return greetingService.getGreeting();
    }
}