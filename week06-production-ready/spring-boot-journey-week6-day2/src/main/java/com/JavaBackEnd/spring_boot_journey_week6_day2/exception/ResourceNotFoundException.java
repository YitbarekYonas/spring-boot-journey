package com.JavaBackEnd.spring_boot_journey_week6_day2.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends LibraryException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(
            resourceName + " not found with id: " + id,
            HttpStatus.NOT_FOUND,
            resourceName.toUpperCase() + "_NOT_FOUND"
        );
    }

    public static ResourceNotFoundException book(Long id) {
        return new ResourceNotFoundException("Book", id);
    }

    public static ResourceNotFoundException author(Long id) {
        return new ResourceNotFoundException("Author", id);
    }

    public static ResourceNotFoundException member(Long id) {
        return new ResourceNotFoundException("Member", id);
    }
}