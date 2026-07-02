package com.JavaBackEnd.spring_boot_journey_Day_5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class SpringBootDay5JourneyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootDay5JourneyApplication.class, args);
    }
}