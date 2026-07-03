package com.JavaBackEnd.greeting.service;

import com.JavaBackEnd.greeting.config.GreetingProperties;
import com.JavaBackEnd.greeting.strategy.GreetingStrategy;
import org.springframework.stereotype.Service;

@Service
public class GreetingService {

    private final GreetingStrategy greetingStrategy;
    private final GreetingProperties greetingProperties;

    public GreetingService(GreetingStrategy greetingStrategy,
                           GreetingProperties greetingProperties) {
        this.greetingStrategy = greetingStrategy;
        this.greetingProperties = greetingProperties;
    }

    public String buildGreeting() {
        String greeting = greetingStrategy.greet(greetingProperties.getRecipient());
        String defaultMessage = greetingProperties.getDefaultMessage();
        String footer = greetingProperties.getFooter();

        return String.format("""
                Greeting: %s
                Message:  %s
                Footer:   %s
                Strategy: %s
                """,
                greeting,
                defaultMessage != null ? defaultMessage : "No default message configured",
                footer != null ? footer : "No footer configured",
                greetingStrategy.strategyName()
        );
    }

    public String getStrategyInfo() {
        return "Active strategy: " + greetingStrategy.strategyName()
               + " | Recipient: " + greetingProperties.getRecipient();
    }
}
