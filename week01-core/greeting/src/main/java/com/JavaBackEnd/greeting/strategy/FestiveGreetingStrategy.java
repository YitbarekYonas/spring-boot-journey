package com.JavaBackEnd.greeting.strategy;

import org.springframework.stereotype.Component;

@Component("festive")
public class FestiveGreetingStrategy implements GreetingStrategy {

    @Override
    public String greet(String recipient) {
        return "🎉 Happy greetings, " + recipient + "! Celebrate today!";
    }

    @Override
    public String strategyName() {
        return "festive";
    }
}
