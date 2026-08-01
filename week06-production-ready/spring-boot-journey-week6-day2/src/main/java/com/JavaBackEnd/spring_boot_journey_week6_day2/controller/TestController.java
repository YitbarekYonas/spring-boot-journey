package com.JavaBackEnd.spring_boot_journey_week6_day2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/exception")
    public String throwException() {
        throw new RuntimeException("Test exception for global handler");
    }
}