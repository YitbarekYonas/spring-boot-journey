package com.JavaBackEnd.spring_boot_journey_week5_day2.config;

import com.JavaBackEnd.spring_boot_journey_week5_day2.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                // ── Public endpoints ──────────────────────────────────────
                .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/authors/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/api/debug/**").permitAll()

                // ── Book management - ADMIN only ────────────────────────
                .requestMatchers(HttpMethod.POST, "/api/books").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")

                // ── Loan management - ADMIN or LIBRARIAN ────────────────
                .requestMatchers(HttpMethod.POST, "/api/loans/checkout").hasAnyRole("ADMIN", "LIBRARIAN")
                // ✅ Fixed: Use {id} path variable instead of /**
                .requestMatchers(HttpMethod.POST, "/api/loans/{id}/return").hasAnyRole("ADMIN", "LIBRARIAN")
                // ✅ Alternative: Use * for single segment
                // .requestMatchers(HttpMethod.POST, "/api/loans/*/return").hasAnyRole("ADMIN", "LIBRARIAN")

                // ── Loan statistics - ADMIN only ────────────────────────
                .requestMatchers(HttpMethod.GET, "/api/loans/stats/**").hasRole("ADMIN")

                // ── Everything else - any authenticated user ─────────────
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}