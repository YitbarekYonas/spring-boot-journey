package com.JavaBackEnd.spring_boot_journey_week5_day3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringBootJourneyWeek5Day3Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyWeek5Day3Application.class, args);

        System.out.println("\n✅ Spring Boot Journey - Week 5 Day 3 Started!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 Auth Endpoints:");
        System.out.println("  POST /api/auth/register");
        System.out.println("  POST /api/auth/login");
        System.out.println("  POST /api/auth/change-password");
        System.out.println("\n📋 Test Users (BCrypt hashed in DB):");
        System.out.println("  admin@library.com / admin123   (ADMIN)");
        System.out.println("  jane@library.com / lib123     (LIBRARIAN)");
        System.out.println("  john@library.com / mem123     (MEMBER)");
    }
}