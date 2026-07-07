package com.JavaBackEnd.spring_boot_journey_week2_day6;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringBootJourneyWeek2Day6Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyWeek2Day6Application.class, args);

        System.out.println("\n✅ Application Started Successfully!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 Layered Architecture:");
        System.out.println("  Controller → Service → Repository");
        System.out.println("\n📋 Testing Endpoints:");
        System.out.println("  GET    http://localhost:8080/api/products");
        System.out.println("  GET    http://localhost:8080/api/products/1");
        System.out.println("  POST   http://localhost:8080/api/products");
        System.out.println("  PUT    http://localhost:8080/api/products/1");
        System.out.println("  DELETE http://localhost:8080/api/products/1");
    }
}