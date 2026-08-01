package com.JavaBackEnd.spring_boot_journey_week6_day2.exception;

import org.springframework.http.HttpStatus;

public class BusinessRuleException extends LibraryException {

    public BusinessRuleException(String message, String errorCode) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, errorCode);
    }

    public static BusinessRuleException bookNotAvailable() {
        return new BusinessRuleException(
            "No copies available for this book",
            "BOOK_NOT_AVAILABLE"
        );
    }

    public static BusinessRuleException alreadyBorrowed() {
        return new BusinessRuleException(
            "Member already has an active loan for this book",
            "ALREADY_BORROWED"
        );
    }
}