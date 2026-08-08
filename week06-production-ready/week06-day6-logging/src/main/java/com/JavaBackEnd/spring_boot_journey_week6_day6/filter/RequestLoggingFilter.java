package com.JavaBackEnd.spring_boot_journey_week6_day6.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

// ── What is OncePerRequestFilter? ─────────────────────────────────────────
// A Spring filter guaranteed to run exactly once per HTTP request.
// Every request passes through this before reaching any controller.
// Perfect for cross-cutting concerns: logging, auth, CORS, rate-limiting.
//
// ── What is MDC? (Mapped Diagnostic Context) ──────────────────────────────
// MDC is a thread-local key-value store that SLF4J/Logback reads automatically.
// When you put a value into MDC, it appears in EVERY log line for that request
// (via %X{requestId} in the logback pattern) — without passing it manually.
//
// This solves a real production problem:
//   8:05:02 INFO  BookService    - Fetching book with id: 7    ← which request?
//   8:05:02 INFO  BookController - Found book: Clean Code      ← same request?
//   8:05:02 ERROR BookService    - Book not found: 99          ← different request?
//
// With MDC requestId:
//   8:05:02 INFO  [a3f1] BookService    - Fetching book with id: 7
//   8:05:02 INFO  [a3f1] BookController - Found book: Clean Code   ← same! a3f1
//   8:05:02 ERROR [b72c] BookService    - Book not found: 99       ← different! b72c
//
// Now you can grep logs for a single requestId and see the full lifecycle.
@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_KEY = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Generate a short unique ID for this request
        //    UUID.randomUUID() = "a3f1b72c-..." → take first 8 chars = "a3f1b72c"
        String requestId = UUID.randomUUID().toString().substring(0, 8);

        // 2. Put it in MDC — now every log.xxx() in this thread includes [a3f1b72c]
        MDC.put(REQUEST_ID_KEY, requestId);

        // 3. Also add to response header so clients/API gateways can trace it
        response.setHeader("X-Request-Id", requestId);

        long startTime = System.currentTimeMillis();

        // Log the incoming request
        log.info("→ {} {} (from: {})",
                request.getMethod(),                        // GET, POST, DELETE …
                request.getRequestURI(),                    // /api/books/1
                request.getRemoteAddr());                   // 127.0.0.1

        try {
            // Let the request continue to the next filter → controller
            filterChain.doFilter(request, response);
        } finally {
            // Log the outgoing response (always runs, even if exception occurred)
            long duration = System.currentTimeMillis() - startTime;

            log.info("← {} {} → {} ({}ms)",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),                   // 200, 201, 404 …
                    duration);                              // how long it took

            // CRITICAL: always clear MDC after the request.
            // Threads are reused (thread pool) — without this, the next request
            // on this thread would inherit the previous request's MDC values.
            MDC.clear();
        }
    }

    // Skip logging for H2 console and actuator endpoints — reduces noise
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/h2-console")
                || uri.startsWith("/actuator")
                || uri.startsWith("/favicon");
    }
}
