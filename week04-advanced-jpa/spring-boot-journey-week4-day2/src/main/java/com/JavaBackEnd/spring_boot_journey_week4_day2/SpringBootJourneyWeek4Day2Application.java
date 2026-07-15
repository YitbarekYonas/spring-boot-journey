package com.JavaBackEnd.spring_boot_journey_week4_day2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringBootJourneyWeek4Day2Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyWeek4Day2Application.class, args);

        System.out.println("\n✅ Spring Boot Journey - Week 4 Day 2 Started!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 Native Query Debug Endpoints:");
        System.out.println("  GET /api/debug/loans/stats/per-book");
        System.out.println("  GET /api/debug/loans/stats/per-member");
    }
}