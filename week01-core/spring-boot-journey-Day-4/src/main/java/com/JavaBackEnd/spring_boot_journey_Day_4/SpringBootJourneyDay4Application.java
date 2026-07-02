package com.JavaBackEnd.spring_boot_journey_Day_4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.JavaBackEnd.spring_boot_journey_Day_4.service.GreetingService;

@SpringBootApplication
public class SpringBootJourneyDay4Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyDay4Application.class, args);
        
        System.out.println("\n✅ Application Started Successfully!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        
        // Test both greetings
        System.out.println("\n📋 Testing Greetings:");
        System.out.println("  Formal: " + context.getBean("formalGreetingService", GreetingService.class).getGreeting());
        System.out.println("  Casual: " + context.getBean("casualGreetingService", GreetingService.class).getGreeting());
    }
}