package com.JavaBackEnd.jwtauthservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication

public class JwtAuthServiceApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(JwtAuthServiceApplication.class, args);

        System.out.println("\n✅ JWT Auth Service Started!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        // ... rest of code
    }
}