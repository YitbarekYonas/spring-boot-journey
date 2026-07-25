package com.JavaBackEnd.spring_boot_journey_week5_day4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringBootJourneyWeek5Day4Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyWeek5Day4Application.class, args);

        System.out.println("\n✅ Spring Boot Journey - Week 5 Day 4 Started!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 JWT Debug Endpoint:");
        System.out.println("  GET /api/debug/jwt/generate/{email}");
        System.out.println("\n📋 Test Users:");
        System.out.println("  admin@library.com / admin123");
        System.out.println("  jane@library.com / lib123");
        System.out.println("  john@library.com / mem123");
    }
}