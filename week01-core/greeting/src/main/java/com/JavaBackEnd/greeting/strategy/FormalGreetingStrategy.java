package com.JavaBackEnd.greeting.strategy;

import org.springframework.stereotype.Component;

@Component("formal")
public class FormalGreetingStrategy implements GreetingStrategy {

    @Override
    public String greet(String recipient) {
        return "Good day, " + recipient + ". I trust you are well.";
    }

    @Override
    public String strategyName() {
        return "formal";
    }
}