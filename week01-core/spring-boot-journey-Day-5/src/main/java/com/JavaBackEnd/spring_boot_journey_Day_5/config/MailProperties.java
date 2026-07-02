package com.JavaBackEnd.spring_boot_journey_Day_5.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "app.mail")
@Validated
public class MailProperties {
    
    @NotBlank(message = "Mail host cannot be blank")
    private String host;
    
    @NotNull(message = "Mail port cannot be null")
    private Integer port;
    
    private boolean ssl;
    
    // Getters and Setters
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public Integer getPort() {
        return port;
    }
    
    public void setPort(Integer port) {
        this.port = port;
    }
    
    public boolean isSsl() {
        return ssl;
    }
    
    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }
}