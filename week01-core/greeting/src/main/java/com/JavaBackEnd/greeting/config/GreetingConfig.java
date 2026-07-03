package com.JavaBackEnd.greeting.config;

import com.JavaBackEnd.greeting.strategy.GreetingStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class GreetingConfig {

    private final GreetingProperties greetingProperties;
    private final GreetingStrategy formalStrategy;
    private final GreetingStrategy casualStrategy;
    private final GreetingStrategy festiveStrategy;

    public GreetingConfig(
            GreetingProperties greetingProperties,
            @Qualifier("formal") GreetingStrategy formalStrategy,
            @Qualifier("casual") GreetingStrategy casualStrategy,
            @Qualifier("festive") GreetingStrategy festiveStrategy) {

        this.greetingProperties = greetingProperties;
        this.formalStrategy = formalStrategy;
        this.casualStrategy = casualStrategy;
        this.festiveStrategy = festiveStrategy;
    }

    @Bean
    @Primary
    public GreetingStrategy activeGreetingStrategy() {
        String active = greetingProperties.getActiveStrategy();

        System.out.println("=== Activating greeting strategy: " + active + " ===");

        return switch (active) {
            case "formal"  -> formalStrategy;
            case "casual"  -> casualStrategy;
            case "festive" -> festiveStrategy;
            default -> throw new IllegalStateException(
                "Unknown greeting strategy: " + active +
                ". Valid values: formal, casual, festive"
            );
        };
    }
}