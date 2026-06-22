package com.JavaBackEnd.spring_boot_journey_Day_2.processor;

import com.JavaBackEnd.spring_boot_journey_Day_2.service.NotificationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessor {
    private final NotificationService notificationService;

    // Constructor Injection (BEST PRACTICE)
    public OrderProcessor(@Qualifier("emailService") NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void processOrder(String orderId) {
        System.out.println("🔄 Processing order: " + orderId);
        notificationService.send("Order " + orderId + " has been processed!");
    }
}