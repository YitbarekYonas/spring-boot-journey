package com.JavaBackEnd.library.controller;

import com.JavaBackEnd.library.dto.BookLoanStats;
import com.JavaBackEnd.library.dto.LoanDetailDto;
import com.JavaBackEnd.library.dto.PageResponse;
import com.JavaBackEnd.library.entity.Loan;
import com.JavaBackEnd.library.entity.LoanStatus;
import com.JavaBackEnd.library.service.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<Loan>> getLoansByStatus(
            @RequestParam(defaultValue = "ACTIVE") LoanStatus status,
            @PageableDefault(size = 10, sort = "loanDate",
                             direction = Sort.Direction.DESC)
            Pageable pageable) {

        Page<Loan> page = loanService.getLoansByStatus(status, pageable);
        return ResponseEntity.ok(new PageResponse<>(page));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<Loan>> getOverdueLoans() {
        return ResponseEntity.ok(loanService.getOverdueLoans());
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<LoanDetailDto>> getLoansByMember(
            @PathVariable Long memberId) {
        return ResponseEntity.ok(
                loanService.getLoanDetailsByMember(memberId));
    }

    @GetMapping("/stats/books")
    public ResponseEntity<List<BookLoanStats>> getBookLoanStats() {
        return ResponseEntity.ok(loanService.getBookLoanStatistics());
    }

    @PostMapping("/checkout")
    public ResponseEntity<Loan> checkout(
            @RequestParam Long bookId,
            @RequestParam Long memberId,
            UriComponentsBuilder uriBuilder) {

        Loan loan = loanService.checkoutBook(bookId, memberId);
        URI location = uriBuilder
                .path("/api/loans/{id}")
                .buildAndExpand(loan.getId())
                .toUri();
        return ResponseEntity.created(location).body(loan);
    }

    @PostMapping("/{loanId}/return")
    public ResponseEntity<Loan> returnBook(@PathVariable Long loanId) {
        return ResponseEntity.ok(loanService.returnBook(loanId));
    }

    @PostMapping("/mark-overdue")
    public ResponseEntity<String> markOverdue() {
        int count = loanService.markOverdueLoans();
        return ResponseEntity.ok(count + " loans marked as overdue");
    }
}