package com.JavaBackEnd.spring_boot_journey_week5_day1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringBootJourneyWeek5Day1Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyWeek5Day1Application.class, args);

        System.out.println("\n✅ Spring Boot Journey - Week 5 Day 1 Started!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 Security Test Users:");
        System.out.println("  admin/admin123   (ADMIN role)");
        System.out.println("  librarian/lib123 (LIBRARIAN role)");
        System.out.println("  member/mem123    (MEMBER role)");
        System.out.println("\n📋 Public Endpoints (No Auth):");
        System.out.println("  GET /api/books");
        System.out.println("  GET /api/books/{id}");
        System.out.println("  GET /api/debug/filters");
        System.out.println("\n📋 Protected Endpoints (Auth Required):");
        System.out.println("  POST /api/books");
        System.out.println("  PUT /api/books/{id}");
        System.out.println("  DELETE /api/books/{id}");
    }
}