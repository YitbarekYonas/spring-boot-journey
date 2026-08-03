package com.JavaBackEnd.spring_boot_journey_week6_day3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing  // ✅ Add this to enable auditing
public class SpringBootJourneyWeek6Day3Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyWeek6Day3Application.class, args);

        System.out.println("\n✅ Spring Boot Journey - Week 6 Day 3 Started!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 Bean Validation Demo:");
        System.out.println("  POST /api/auth/register - Register with validation");
        System.out.println("  POST /api/auth/login    - Login with validation");
        System.out.println("  POST /api/auth/change-password - Change password with validation");
    }
}