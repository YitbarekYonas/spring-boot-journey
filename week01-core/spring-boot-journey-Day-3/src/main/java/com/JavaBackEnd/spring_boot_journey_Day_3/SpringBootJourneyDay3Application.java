package com.JavaBackEnd.spring_boot_journey_Day_3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringBootJourneyDay3Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyDay3Application.class, args);
        
        System.out.println("\n✅ Application Started Successfully!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        
        // Test singleton scope
        Object bean1 = context.getBean("greetingService");
        Object bean2 = context.getBean("greetingService");
        
        System.out.println("\n🔍 Singleton Scope Test:");
        System.out.println("  Bean 1 hash: " + System.identityHashCode(bean1));
        System.out.println("  Bean 2 hash: " + System.identityHashCode(bean2));
        System.out.println("  Same instance? " + (bean1 == bean2));
    }
}