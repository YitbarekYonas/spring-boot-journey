package com.JavaBackEnd.spring_boot_journey_Day_3.config;

import com.JavaBackEnd.spring_boot_journey_Day_3.service.GreetingService;
import com.JavaBackEnd.spring_boot_journey_Day_3.service.TimeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AppConfig {
    
    // Bean 1: GreetingService
    @Bean
    public GreetingService greetingService() {
        GreetingService service = new GreetingService();
        service.setPrefix("👋 Welcome");
        return service;
    }
    
    // Bean 2: TimeService
    @Bean
    public TimeService timeService() {
        return new TimeService();
    }
}