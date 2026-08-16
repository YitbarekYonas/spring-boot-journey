package com.JavaBackEnd.spring_boot_journey_week7_day6.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

// Consistent error response shape (Week 6, Day 2) — every error from this
// API looks the same: when it happened, what status, what went wrong, and
// where. `fieldErrors` is populated only for validation failures.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private String path;
    private List<String> fieldErrors;
}
