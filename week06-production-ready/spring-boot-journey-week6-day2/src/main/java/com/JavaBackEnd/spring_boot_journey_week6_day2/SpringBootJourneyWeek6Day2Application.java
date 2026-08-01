package com.JavaBackEnd.spring_boot_journey_week6_day2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringBootJourneyWeek6Day2Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyWeek6Day2Application.class, args);

        System.out.println("\n✅ Spring Boot Journey - Week 6 Day 2 Started!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 Global Exception Handling Demo:");
        System.out.println("  GET  /api/books           - Get all books");
        System.out.println("  POST /api/books           - Create book (validation)");
        System.out.println("  GET  /api/books/{id}      - Get book by ID (404 test)");
        System.out.println("  GET  /api/books/abc       - Type mismatch (400)");
        System.out.println("  GET  /api/test/exception  - Test generic exception");
    }
}