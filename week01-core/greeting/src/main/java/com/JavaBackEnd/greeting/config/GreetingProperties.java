package com.JavaBackEnd.greeting.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "app.greeting")
@Validated
public class GreetingProperties {

    @NotBlank(message = "active-strategy must be specified")
    @Pattern(
        regexp = "formal|casual|festive",
        message = "active-strategy must be one of: formal, casual, festive"
    )
    private String activeStrategy;

    @NotBlank(message = "recipient must be specified")
    private String recipient;

    private String defaultMessage;
    private String footer;

    public String getActiveStrategy() { return activeStrategy; }
    public void setActiveStrategy(String activeStrategy) { this.activeStrategy = activeStrategy; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public String getDefaultMessage() { return defaultMessage; }
    public void setDefaultMessage(String defaultMessage) { this.defaultMessage = defaultMessage; }

    public String getFooter() { return footer; }
    public void setFooter(String footer) { this.footer = footer; }
}
