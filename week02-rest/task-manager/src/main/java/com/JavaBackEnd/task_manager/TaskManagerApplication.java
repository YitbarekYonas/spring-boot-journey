package com.JavaBackEnd.task_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TaskManagerApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(TaskManagerApplication.class, args);

        System.out.println("\n✅ Task Manager Application Started Successfully!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        System.out.println("\n📋 API Endpoints:");
        System.out.println("  GET    /api/tasks");
        System.out.println("  GET    /api/tasks/overdue");
        System.out.println("  GET    /api/tasks/{id}");
        System.out.println("  POST   /api/tasks");
        System.out.println("  PUT    /api/tasks/{id}");
        System.out.println("  PATCH  /api/tasks/{id}/status");
        System.out.println("  DELETE /api/tasks/{id}");
    }
}