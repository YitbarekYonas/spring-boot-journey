package com.JavaBackEnd.spring_boot_journey_week5_day5.config;

import com.JavaBackEnd.spring_boot_journey_week5_day5.security.CustomUserDetailsService;
import com.JavaBackEnd.spring_boot_journey_week5_day5.security.JwtAccessDeniedHandler;
import com.JavaBackEnd.spring_boot_journey_week5_day5.security.JwtAuthenticationEntryPoint;
import com.JavaBackEnd.spring_boot_journey_week5_day5.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          JwtAuthenticationFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public JwtAuthenticationEntryPoint authenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint(objectMapper());
    }

    @Bean
    public JwtAccessDeniedHandler accessDeniedHandler() {
        return new JwtAccessDeniedHandler(objectMapper());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                // ── Auth endpoints - public ─────────────────────────────
                .requestMatchers("/api/auth/**").permitAll()

                // ── Public read access ────────────────────────────────────
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
                .requestMatchers(HttpMethod.POST, "/api/loans/{id}/return").hasAnyRole("ADMIN", "LIBRARIAN")

                // ── Loan statistics - ADMIN only ────────────────────────
                .requestMatchers(HttpMethod.GET, "/api/loans/stats/**").hasRole("ADMIN")

                // ── Everything else - any authenticated user ─────────────
                .anyRequest().authenticated()
            )

            // ── JWT Filter ─────────────────────────────────────────────────
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

            // ── Exception Handling ────────────────────────────────────────
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
            )

            // ── Session Management ────────────────────────────────────────
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // ── CSRF ──────────────────────────────────────────────────────
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