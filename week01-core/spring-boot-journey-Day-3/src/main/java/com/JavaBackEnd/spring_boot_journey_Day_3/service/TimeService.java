package com.JavaBackEnd.spring_boot_journey_Day_3.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeService {
    private final String format = "HH:mm:ss";
    
    public String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));
    }
}