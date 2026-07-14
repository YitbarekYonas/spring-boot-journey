package com.JavaBackEnd.spring_boot_journey_week4_day1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringBootJourneyWeek4Day1Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyWeek4Day1Application.class, args);

        System.out.println("\n✅ Spring Boot Journey - Week 4 Day 1 Started!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 JPQL Debug Endpoints:");
        System.out.println("  GET /api/debug/books/search?keyword=clean");
        System.out.println("  GET /api/debug/books/genre/Software%20Engineering");
        System.out.println("  GET /api/debug/books/author/Martin");
        System.out.println("  GET /api/debug/books/stats/genre-counts");
        System.out.println("  GET /api/debug/books/on-loan");
        System.out.println("  GET /api/debug/loans/overdue-with-details");
    }
}