package com.JavaBackEnd.spring_boot_journey_week6_day4.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// ── Why permit all here? ───────────────────────────────────────────────────
// Week 6 Day 4 is about DTOs — not about authentication.
// JWT security was built in Week 5. To keep today's focus clean,
// we open all endpoints so you can test every DTO endpoint in Postman
// without needing auth headers.
//
// In the real project (capstone), JWT filter plugs in here and
// .requestMatchers("/api/auth/**").permitAll()
// .anyRequest().authenticated()
// replaces the permitAll() below.
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)     // disable for REST APIs
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()  // H2 web console
                .anyRequest().permitAll()   // ← open for DTO demo; lock down in prod
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin()) // needed for H2 console iframe
            );

        return http.build();
    }

    // BCryptPasswordEncoder — used in Week6Day4Application seedData to hash passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
