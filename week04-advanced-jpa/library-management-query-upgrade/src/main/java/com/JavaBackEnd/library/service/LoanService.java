package com.JavaBackEnd.library.service;

import com.JavaBackEnd.library.dto.BookLoanStats;
import com.JavaBackEnd.library.dto.LoanDetailDto;
import com.JavaBackEnd.library.entity.Loan;
import com.JavaBackEnd.library.entity.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LoanService {

    Page<Loan> getLoansByStatus(LoanStatus status, Pageable pageable);

    List<LoanDetailDto> getLoanDetailsByMember(Long memberId);

    List<Loan> getOverdueLoans();

    List<BookLoanStats> getBookLoanStatistics();

    Loan checkoutBook(Long bookId, Long memberId);

    Loan returnBook(Long loanId);

    int markOverdueLoans();
}