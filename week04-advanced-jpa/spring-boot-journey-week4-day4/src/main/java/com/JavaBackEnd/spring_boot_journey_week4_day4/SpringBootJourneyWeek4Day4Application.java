package com.JavaBackEnd.spring_boot_journey_week4_day4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringBootJourneyWeek4Day4Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyWeek4Day4Application.class, args);

        System.out.println("\n✅ Spring Boot Journey - Week 4 Day 4 Started!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 DTO Query Endpoints:");
        System.out.println("  GET /api/books/full     - Full entity (many columns)");
        System.out.println("  GET /api/books/summary  - DTO (7 columns only)");
        System.out.println("  GET /api/books          - Paginated DTO");
    }
}