package com.JavaBackEnd.spring_boot_journey_week7_day7;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Week 7 capstone: full layered test suite across Mockito, @WebMvcTest,
// @DataJpaTest, and Testcontainers — all against this one Task Manager app.
@SpringBootApplication
public class Week7Day7Application {
    public static void main(String[] args) {
        SpringApplication.run(Week7Day7Application.class, args);
    }
}
