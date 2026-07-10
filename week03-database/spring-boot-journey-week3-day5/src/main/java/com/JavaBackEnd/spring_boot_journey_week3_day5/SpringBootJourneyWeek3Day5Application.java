package com.JavaBackEnd.spring_boot_journey_week3_day5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringBootJourneyWeek3Day5Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyWeek3Day5Application.class, args);

        System.out.println("\n✅ Application Started Successfully!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 Endpoints:");
        System.out.println("  GET /api/tasks/all              ← N+1 problem!");
        System.out.println("  GET /api/tasks/all-with-tags    ← JOIN FETCH fix");
        System.out.println("  GET /api/tasks/by-tag/{name}");
        System.out.println("  GET /api/tags");
        System.out.println("  GET /api/tags/in-use");
    }
}