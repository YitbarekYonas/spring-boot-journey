package com.JavaBackEnd.spring_boot_journey_Day_3.controller;

import com.JavaBackEnd.spring_boot_journey_Day_3.service.GreetingService;
import com.JavaBackEnd.spring_boot_journey_Day_3.service.TimeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/greeting")
public class GreetingController {
    
    private final GreetingService greetingService;
    private final TimeService timeService;
    
    public GreetingController(GreetingService greetingService, TimeService timeService) {
        this.greetingService = greetingService;
        this.timeService = timeService;
    }
    
    @GetMapping
    public Map<String, String> getGreeting() {
        return Map.of(
            "greeting", greetingService.getGreeting(),
            "time", timeService.getCurrentTime()
        );
    }
}