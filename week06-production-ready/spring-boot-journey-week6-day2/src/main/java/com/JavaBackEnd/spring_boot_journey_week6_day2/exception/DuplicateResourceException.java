package com.JavaBackEnd.spring_boot_journey_week6_day2.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends LibraryException {

    public DuplicateResourceException(String resourceName, String field, String value) {
        super(
            resourceName + " already exists with " + field + ": " + value,
            HttpStatus.CONFLICT,
            "DUPLICATE_" + resourceName.toUpperCase()
        );
    }

    public static DuplicateResourceException isbn(String isbn) {
        return new DuplicateResourceException("Book", "ISBN", isbn);
    }

    public static DuplicateResourceException email(String email) {
        return new DuplicateResourceException("User", "email", email);
    }
}