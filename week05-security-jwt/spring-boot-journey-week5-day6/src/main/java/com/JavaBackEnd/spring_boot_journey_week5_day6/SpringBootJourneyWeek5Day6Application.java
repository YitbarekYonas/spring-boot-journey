package com.JavaBackEnd.spring_boot_journey_week5_day6;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringBootJourneyWeek5Day6Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyWeek5Day6Application.class, args);

        System.out.println("\n✅ Spring Boot Journey - Week 5 Day 6 Started!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 Refresh Token Flow:");
        System.out.println("  1. POST /api/auth/login → get access + refresh tokens");
        System.out.println("  2. POST /api/auth/refresh → get new access token");
        System.out.println("  3. POST /api/auth/logout → revoke refresh token");
        System.out.println("  4. POST /api/auth/logout-all → revoke ALL refresh tokens");
        System.out.println("\n📋 Test Users:");
        System.out.println("  admin@library.com / admin123");
        System.out.println("  jane@library.com / lib123");
        System.out.println("  john@library.com / mem123");
    }
}