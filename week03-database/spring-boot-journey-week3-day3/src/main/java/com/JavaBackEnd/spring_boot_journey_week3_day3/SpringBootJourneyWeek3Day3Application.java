package com.JavaBackEnd.spring_boot_journey_week3_day3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringBootJourneyWeek3Day3Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyWeek3Day3Application.class, args);

        System.out.println("\n✅ Application Started Successfully!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 Debug Endpoints:");
        System.out.println("  GET /api/debug/tasks/all");
        System.out.println("  GET /api/debug/tasks/by-status/{status}");
        System.out.println("  GET /api/debug/tasks/by-priority/{priority}");
        System.out.println("  GET /api/debug/tasks/search?keyword=...");
        System.out.println("  GET /api/debug/tasks/overdue");
        System.out.println("  GET /api/debug/tasks/count-by-status/{status}");
        System.out.println("  GET /api/debug/tasks/top5-recent");
        System.out.println("  GET /api/debug/tasks/due-before?date=2026-07-09");
        System.out.println("  GET /api/debug/tasks/due-after?date=2026-07-09");
    }
}