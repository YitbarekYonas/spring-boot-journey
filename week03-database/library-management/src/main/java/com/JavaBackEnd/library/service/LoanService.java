package com.JavaBackEnd.library.service;

import com.JavaBackEnd.library.entity.Loan;
import com.JavaBackEnd.library.entity.LoanStatus;

import java.util.List;

public interface LoanService {

    List<Loan> getAllLoans();

    List<Loan> getLoansByMember(Long memberId);

    List<Loan> getOverdueLoans();

    long countByStatus(LoanStatus status);

    Loan checkoutBook(Long bookId, Long memberId);

    Loan returnBook(Long loanId);

    int markOverdueLoans();
}