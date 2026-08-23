package com.JavaBackEnd.spring_boot_journey_week8_day7;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Week 8, Day 1 focus: Docker Fundamentals. Nothing new about the app
// itself — the point of today is packaging THIS app into a container.
@SpringBootApplication
public class TaskManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }
}
