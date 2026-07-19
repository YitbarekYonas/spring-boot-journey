package com.JavaBackEnd.spring_boot_journey_week4_day6;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication   // ❌ Remove @EnableJpaAuditing from here
public class SpringBootJourneyWeek4Day6Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyWeek4Day6Application.class, args);

        System.out.println("\n✅ Spring Boot Journey - Week 4 Day 6 Started!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 Auditing & Query Optimization Endpoints:");
        System.out.println("  POST /api/books");
        System.out.println("  PUT  /api/books/{id}");
        System.out.println("  GET  /api/books        - JOIN FETCH (No N+1)");
        System.out.println("  GET  /api/books/n-plus-one - N+1 Demo");
    }
}