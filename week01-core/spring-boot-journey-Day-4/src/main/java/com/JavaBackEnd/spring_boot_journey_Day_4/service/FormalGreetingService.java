package com.JavaBackEnd.spring_boot_journey_Day_4.service;

import org.springframework.stereotype.Service;

@Service
public class FormalGreetingService implements GreetingService {
    @Override
    public String getGreeting() {
        return "Good morning. Welcome to our service.";
    }
}