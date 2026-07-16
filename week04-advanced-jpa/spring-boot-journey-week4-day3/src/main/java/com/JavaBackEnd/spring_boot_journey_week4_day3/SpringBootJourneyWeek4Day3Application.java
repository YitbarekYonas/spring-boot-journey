package com.JavaBackEnd.spring_boot_journey_week4_day3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringBootJourneyWeek4Day3Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyWeek4Day3Application.class, args);

        System.out.println("\n✅ Spring Boot Journey - Week 4 Day 3 Started!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 Pagination Endpoints:");
        System.out.println("  GET /api/books?page=0&size=10&sort=title,asc");
        System.out.println("  GET /api/books/available?page=0&size=5");
        System.out.println("  GET /api/books/search?keyword=clean&page=0&size=5");
        System.out.println("  GET /api/books/genre/Software%20Engineering?page=0&size=5");
    }
}