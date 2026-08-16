package com.JavaBackEnd.spring_boot_journey_week7_day6;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Day 6 focus: Full end-to-end tests — real HTTP calls, through the real
// controller/service/repository stack, against a real Postgres container.
@SpringBootApplication
public class Week7Day6Application {
    public static void main(String[] args) {
        SpringApplication.run(Week7Day6Application.class, args);
    }
}
