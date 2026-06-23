package com.JavaBackEnd.spring_boot_journey_Day_3.service;

public class GreetingService {
    private String prefix = "Hello";
    
    public String getGreeting() {
        return prefix + " from Spring!";
    }
    
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}