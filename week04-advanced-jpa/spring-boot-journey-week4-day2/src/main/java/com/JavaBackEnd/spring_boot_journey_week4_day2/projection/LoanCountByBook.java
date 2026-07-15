package com.JavaBackEnd.spring_boot_journey_week4_day2.projection;

public interface LoanCountByBook {
    Long getBookId();
    String getBookTitle();
    String getBookIsbn();
    String getAuthorName();
    Long getTotalLoans();
    Long getActiveLoans();
    Long getReturnedLoans();
}