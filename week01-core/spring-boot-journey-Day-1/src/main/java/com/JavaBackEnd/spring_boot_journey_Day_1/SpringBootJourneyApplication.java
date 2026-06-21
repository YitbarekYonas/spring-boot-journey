package com.JavaBackEnd.spring_boot_journey_Day_1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringBootJourneyApplication {
    public static void main(String[] args) {
        // Get the ApplicationContext
        ApplicationContext context = SpringApplication.run(SpringBootJourneyApplication.class, args);
        
        // Get all bean names
        String[] beanNames = context.getBeanDefinitionNames();
        
        // Print the count
        System.out.println("✅ Application Started Successfully!");
        System.out.println("📊 Spring registered " + beanNames.length + " beans automatically!");
        System.out.println("\n📋 First 10 beans:");
        
        // Print first 10 beans to see what Spring auto-configured
        for (int i = 0; i < Math.min(10, beanNames.length); i++) {
            System.out.println("  - " + beanNames[i]);
        }
    }
}