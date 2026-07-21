package com.JavaBackEnd.spring_boot_journey_week5_day1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            // ── Authorization rules ──────────────────────────────────────
            .authorizeHttpRequests(auth -> auth
                // ✅ Public GET endpoints - no authentication required
                .requestMatchers(HttpMethod.GET, "/api/books").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
                .requestMatchers("/api/debug/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()

                // ✅ All other requests require authentication
                .anyRequest().authenticated()
            )

            // ── Authentication mechanism ──────────────────────────────────
            .httpBasic(Customizer.withDefaults())

            // ── Session management ────────────────────────────────────────
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // ── CSRF ──────────────────────────────────────────────────────
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails librarian = User.builder()
                .username("librarian")
                .password(encoder.encode("lib123"))
                .roles("LIBRARIAN")
                .build();

        UserDetails member = User.builder()
                .username("member")
                .password(encoder.encode("mem123"))
                .roles("MEMBER")
                .build();

        return new InMemoryUserDetailsManager(admin, librarian, member);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}