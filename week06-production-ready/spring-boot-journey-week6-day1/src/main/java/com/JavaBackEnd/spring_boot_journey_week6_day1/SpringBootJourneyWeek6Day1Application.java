package com.JavaBackEnd.spring_boot_journey_week6_day1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableJpaAuditing
@EnableMethodSecurity   // ✅ Enables @PreAuthorize
public class SpringBootJourneyWeek6Day1Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyWeek6Day1Application.class, args);

        System.out.println("\n✅ Spring Boot Journey - Week 6 Day 1 Started!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 Role-Based Authorization Demo:");
        System.out.println("  ADMIN  - Full access");
        System.out.println("  USER   - Limited access");
        System.out.println("\n📋 Test Users:");
        System.out.println("  admin@example.com / admin123 (ADMIN)");
        System.out.println("  user@example.com / user123 (USER)");
    }
}