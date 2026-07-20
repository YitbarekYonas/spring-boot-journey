package com.JavaBackEnd.library.service.impl;

import com.JavaBackEnd.library.dto.BookLoanStats;
import com.JavaBackEnd.library.dto.LoanDetailDto;
import com.JavaBackEnd.library.entity.Book;
import com.JavaBackEnd.library.entity.Loan;
import com.JavaBackEnd.library.entity.LoanStatus;
import com.JavaBackEnd.library.entity.Member;
import com.JavaBackEnd.library.repository.BookRepository;
import com.JavaBackEnd.library.repository.LoanRepository;
import com.JavaBackEnd.library.repository.MemberRepository;
import com.JavaBackEnd.library.service.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LoanServiceImpl implements LoanService {

    private static final int DEFAULT_LOAN_DAYS = 14;

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public LoanServiceImpl(LoanRepository loanRepository,
                           BookRepository bookRepository,
                           MemberRepository memberRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public Page<Loan> getLoansByStatus(LoanStatus status, Pageable pageable) {
        return loanRepository.findByStatusWithDetails(status, pageable);
    }

    @Override
    public List<LoanDetailDto> getLoanDetailsByMember(Long memberId) {
        return loanRepository.findLoanDetailsByMemberId(memberId);
    }

    @Override
    public List<Loan> getOverdueLoans() {
        return loanRepository.findOverdueLoans(LocalDate.now());
    }

    @Override
    public List<BookLoanStats> getBookLoanStatistics() {
        return loanRepository.getBookLoanStatistics();
    }

    @Override
    @Transactional
    public Loan checkoutBook(Long bookId, Long memberId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Book not found: " + bookId));

        if (!book.isAvailable()) {
            throw new IllegalStateException(
                "No copies available for: " + book.getTitle());
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Member not found: " + memberId));

        if (!member.isActive()) {
            throw new IllegalStateException(
                "Member account is not active: " + member.getEmail());
        }

        boolean alreadyBorrowed = loanRepository
                .existsByBookIdAndMemberIdAndStatus(
                    bookId, memberId, LoanStatus.ACTIVE);

        if (alreadyBorrowed) {
            throw new IllegalStateException(
                "Member already has an active loan for this book");
        }

        book.checkout();
        Loan loan = new Loan(book, member, DEFAULT_LOAN_DAYS);
        return loanRepository.save(loan);
    }

    @Override
    @Transactional
    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Loan not found: " + loanId));

        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new IllegalStateException(
                "Book already returned for loan: " + loanId);
        }

        loan.returnBook();
        loan.getBook().returnCopy();
        return loan;
    }

    @Override
    @Transactional
    public int markOverdueLoans() {
        List<Loan> overdueLoans = loanRepository
                .findOverdueLoans(LocalDate.now());
        overdueLoans.forEach(Loan::markOverdue);
        loanRepository.saveAll(overdueLoans);
        return overdueLoans.size();
    }
}