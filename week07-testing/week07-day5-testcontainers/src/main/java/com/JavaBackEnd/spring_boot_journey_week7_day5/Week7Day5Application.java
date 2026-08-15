package com.JavaBackEnd.spring_boot_journey_week7_day5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Day 5 focus: Integration Testing with Testcontainers — run with: mvn test
// (requires Docker running locally / in CI, since Testcontainers spins up a
// real PostgreSQL container for the test suite)
@SpringBootApplication
public class Week7Day5Application {
    public static void main(String[] args) {
        SpringApplication.run(Week7Day5Application.class, args);
    }
}
