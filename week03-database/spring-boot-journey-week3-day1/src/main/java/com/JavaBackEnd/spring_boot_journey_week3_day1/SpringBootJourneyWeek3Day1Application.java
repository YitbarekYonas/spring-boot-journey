package com.JavaBackEnd.spring_boot_journey_week3_day1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class SpringBootJourneyWeek3Day1Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyWeek3Day1Application.class, args);

        System.out.println("\n✅ Application Started Successfully!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());

        Environment env = context.getEnvironment();
        System.out.println("\n📋 Database Configuration:");
        System.out.println("  URL: " + env.getProperty("spring.datasource.url"));
        System.out.println("  Driver: " + env.getProperty("spring.datasource.driver-class-name", "PostgreSQL Driver"));
        System.out.println("  JPA ddl-auto: " + env.getProperty("spring.jpa.hibernate.ddl-auto"));
    }
}