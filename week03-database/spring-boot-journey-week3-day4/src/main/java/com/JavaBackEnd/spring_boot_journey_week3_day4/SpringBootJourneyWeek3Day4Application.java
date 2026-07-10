package com.JavaBackEnd.spring_boot_journey_week3_day4;

import com.JavaBackEnd.spring_boot_journey_week3_day4.service.LazyLoadingDemoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootJourneyWeek3Day4Application {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyWeek3Day4Application.class, args);

        System.out.println("\n✅ Application Started Successfully!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 Debug Endpoints:");
        System.out.println("  GET /api/debug/tasks");
        System.out.println("  GET /api/debug/tasks/{id}");
        System.out.println("  GET /api/debug/users");
        System.out.println("  GET /api/debug/users/{id}/tasks");
    }

    @Bean
    public CommandLineRunner demoLazyLoading(LazyLoadingDemoService demoService) {
        return args -> {
            // Wait a moment for the app to fully start
            Thread.sleep(3000);

            // Find a task ID to test with
            System.out.println("\n========================================");
            System.out.println("   LAZY LOADING DEMO");
            System.out.println("========================================");

            // Assuming task ID 1 exists from seed data
            Long taskId = 1L;

            // 1. Demonstrate LAZY loading works inside transaction
            demoService.demonstrateLazyLoadingWorks(taskId);

            // 2. Demonstrate LazyInitializationException
            demoService.demonstrateLazyLoadingException(taskId);

            // 3. Demonstrate Fetch Join
            demoService.demonstrateFetchJoin(taskId);

            System.out.println("\n========================================\n");
        };
    }
}