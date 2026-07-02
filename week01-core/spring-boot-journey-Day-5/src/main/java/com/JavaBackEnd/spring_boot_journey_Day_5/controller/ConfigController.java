package com.JavaBackEnd.spring_boot_journey_Day_5.controller;

import com.JavaBackEnd.spring_boot_journey_Day_5.config.MailProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/config")
public class ConfigController {
    
    private final MailProperties mailProperties;
    
    public ConfigController(MailProperties mailProperties) {
        this.mailProperties = mailProperties;
    }
    
    @GetMapping("/mail")
    public Map<String, Object> getMailConfig() {
        return Map.of(
            "host", mailProperties.getHost(),
            "port", mailProperties.getPort(),
            "ssl", mailProperties.isSsl(),
            "activeProfile", System.getProperty("spring.profiles.active", "default")
        );
    }
}