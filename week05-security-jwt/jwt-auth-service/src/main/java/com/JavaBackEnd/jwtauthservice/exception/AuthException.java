package com.JavaBackEnd.jwtauthservice.exception;

import org.springframework.http.HttpStatus;

public class AuthException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public AuthException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public static AuthException emailAlreadyExists(String email) {
        return new AuthException(
            "Email already registered: " + email,
            HttpStatus.CONFLICT,
            "EMAIL_ALREADY_EXISTS"
        );
    }

    public static AuthException invalidCredentials() {
        return new AuthException(
            "Invalid email or password",
            HttpStatus.UNAUTHORIZED,
            "INVALID_CREDENTIALS"
        );
    }

    public static AuthException weakPassword() {
        return new AuthException(
            "Password must be 8-72 characters long.",
            HttpStatus.BAD_REQUEST,
            "WEAK_PASSWORD"
        );
    }

    public static AuthException incorrectCurrentPassword() {
        return new AuthException(
            "Current password is incorrect.",
            HttpStatus.BAD_REQUEST,
            "INCORRECT_PASSWORD"
        );
    }

    public static AuthException tokenNotFound() {
        return new AuthException(
            "Refresh token not found.",
            HttpStatus.UNAUTHORIZED,
            "TOKEN_NOT_FOUND"
        );
    }

    public static AuthException tokenRevoked() {
        return new AuthException(
            "Refresh token has been revoked. All sessions invalidated.",
            HttpStatus.UNAUTHORIZED,
            "TOKEN_REVOKED"
        );
    }

    public static AuthException tokenExpired() {
        return new AuthException(
            "Refresh token has expired. Please log in again.",
            HttpStatus.UNAUTHORIZED,
            "TOKEN_EXPIRED"
        );
    }

    public HttpStatus getStatus() { return status; }
    public String getErrorCode() { return errorCode; }
}