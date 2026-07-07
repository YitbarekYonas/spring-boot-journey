package com.JavaBackEnd.spring_boot_journey_week2_day5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringBootJourneyWeek2Day5Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyWeek2Day5Application.class, args);

        System.out.println("\n✅ Application Started Successfully!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 Testing Endpoints:");
        System.out.println("  GET    http://localhost:8080/api/products");
        System.out.println("  GET    http://localhost:8080/api/products/1");
        System.out.println("  POST   http://localhost:8080/api/products");
        System.out.println("  PUT    http://localhost:8080/api/products/1");
        System.out.println("  DELETE http://localhost:8080/api/products/1");
        System.out.println("\n📋 Headers to Test:");
        System.out.println("  X-API-Version: 1 (valid) or 99 (invalid)");
        System.out.println("  X-Request-Id: your-custom-id");
        System.out.println("\n🌐 CORS Allowed Origin: http://localhost:3000");
    }
}