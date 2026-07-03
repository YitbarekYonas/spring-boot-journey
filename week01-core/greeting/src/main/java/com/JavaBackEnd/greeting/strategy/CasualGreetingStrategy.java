package com.JavaBackEnd.greeting.strategy;

import org.springframework.stereotype.Component;

@Component("casual")
public class CasualGreetingStrategy implements GreetingStrategy {

    @Override
    public String greet(String recipient) {
        return "Hey " + recipient + "! What's up?";
    }

    @Override
    public String strategyName() {
        return "casual";
    }
}