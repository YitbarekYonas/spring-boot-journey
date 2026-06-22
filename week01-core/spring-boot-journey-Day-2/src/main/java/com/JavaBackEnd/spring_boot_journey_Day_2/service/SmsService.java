package com.JavaBackEnd.spring_boot_journey_Day_2.service;

import org.springframework.stereotype.Service;

@Service
public class SmsService implements NotificationService {
    @Override
    public void send(String message) {
        System.out.println("📱 Sending SMS: " + message);
    }
}