package com.JavaBackEnd.jwtauthservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalAuthExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, Object>> handleAuthException(
            AuthException ex, WebRequest request) {

        Map<String, Object> body = buildErrorBody(
            ex.getStatus().value(),
            ex.getErrorCode(),
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(
            BadCredentialsException ex, WebRequest request) {

        Map<String, Object> body = buildErrorBody(
            401,
            "INVALID_CREDENTIALS",
            "Invalid email or password",
            request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, Object>> handleDisabled(
            DisabledException ex, WebRequest request) {

        Map<String, Object> body = buildErrorBody(
            401,
            "ACCOUNT_DISABLED",
            "Account is disabled. Contact support.",
            request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Map<String, Object>> handleLocked(
            LockedException ex, WebRequest request) {

        Map<String, Object> body = buildErrorBody(
            401,
            "ACCOUNT_LOCKED",
            "Account is locked. Contact support.",
            request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    private Map<String, Object> buildErrorBody(int status, String errorCode,
                                                String message, String path) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status);
        body.put("errorCode", errorCode);
        body.put("message", message);
        body.put("path", path);
        return body;
    }
}