package com.JavaBackEnd.greeting.strategy;

public interface GreetingStrategy {
    String greet(String recipient);
    String strategyName();
}