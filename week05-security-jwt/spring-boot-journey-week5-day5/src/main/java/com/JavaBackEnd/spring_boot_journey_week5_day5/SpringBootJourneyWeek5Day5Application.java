package com.JavaBackEnd.spring_boot_journey_week5_day5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringBootJourneyWeek5Day5Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyWeek5Day5Application.class, args);

        System.out.println("\n✅ Spring Boot Journey - Week 5 Day 5 Started!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 JWT Authentication Flow:");
        System.out.println("  1. POST /api/auth/login → get JWT token");
        System.out.println("  2. GET /api/auth/me → test token (Authorization: Bearer <token>)");
        System.out.println("\n📋 Test Users:");
        System.out.println("  admin@library.com / admin123");
        System.out.println("  jane@library.com / lib123");
        System.out.println("  john@library.com / mem123");
    }
}