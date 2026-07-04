package com.JavaBackEnd.spring_boot_journey_week2_day1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringBootJourneyWeek2Day1Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyWeek2Day1Application.class, args);
        
        System.out.println("\n✅ Application Started Successfully!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 Testing endpoint:");
        System.out.println("  GET http://localhost:8080/api/products");
    }
}