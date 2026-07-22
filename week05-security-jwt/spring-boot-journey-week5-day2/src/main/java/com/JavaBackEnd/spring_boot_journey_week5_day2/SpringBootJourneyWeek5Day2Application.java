package com.JavaBackEnd.spring_boot_journey_week5_day2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringBootJourneyWeek5Day2Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyWeek5Day2Application.class, args);

        System.out.println("\n✅ Spring Boot Journey - Week 5 Day 2 Started!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 Test Users (seeded in database):");
        System.out.println("  admin@library.com / admin123   (ADMIN)");
        System.out.println("  jane@library.com / lib123     (LIBRARIAN)");
        System.out.println("  john@library.com / mem123     (MEMBER)");
        System.out.println("\n📋 Test Endpoints:");
        System.out.println("  GET  /api/books           - Public");
        System.out.println("  POST /api/books           - ADMIN only");
        System.out.println("  GET  /api/loans/my-loans  - MEMBER only");
        System.out.println("  GET  /api/loans/stats     - ADMIN only");
    }
}