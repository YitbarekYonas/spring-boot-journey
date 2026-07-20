package com.JavaBackEnd.library.dto;

public interface BookLoanStats {
    Long getBookId();
    String getBookTitle();
    String getBookIsbn();
    String getAuthorName();
    Long getTotalLoans();
    Long getActiveLoans();
    Long getReturnedLoans();
}