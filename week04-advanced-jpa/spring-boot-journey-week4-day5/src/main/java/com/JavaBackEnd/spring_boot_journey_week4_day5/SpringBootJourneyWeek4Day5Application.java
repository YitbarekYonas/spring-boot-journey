package com.JavaBackEnd.spring_boot_journey_week4_day5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringBootJourneyWeek4Day5Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyWeek4Day5Application.class, args);

        System.out.println("\n✅ Spring Boot Journey - Week 4 Day 5 Started!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 Transaction Demo Endpoints:");
        System.out.println("  GET  /api/transfer/branches");
        System.out.println("  POST /api/transfer/with-transaction?from=1&to=2&copies=1");
        System.out.println("  POST /api/transfer/without-transaction?from=1&to=2&copies=1");
    }
}