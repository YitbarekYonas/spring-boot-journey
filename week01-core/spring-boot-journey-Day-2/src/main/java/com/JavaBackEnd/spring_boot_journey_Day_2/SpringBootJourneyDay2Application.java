package com.JavaBackEnd.spring_boot_journey_Day_2;

import com.JavaBackEnd.spring_boot_journey_Day_2.processor.OrderProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpringBootJourneyDay2Application {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootJourneyDay2Application.class, args);
        
        System.out.println("\n✅ Application Started Successfully!");
        System.out.println("📊 Beans: " + context.getBeanDefinitionCount());
        
        // Get the OrderProcessor bean and use it
        OrderProcessor processor = context.getBean(OrderProcessor.class);
        processor.processOrder("ORD-123");
    }
}