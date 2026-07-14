package com.JavaBackEnd.spring_boot_journey_week4_day1.repository;

import com.JavaBackEnd.spring_boot_journey_week4_day1.entity.Loan;
import com.JavaBackEnd.spring_boot_journey_week4_day1.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByStatus(LoanStatus status);
    List<Loan> findByMemberId(Long memberId);
    List<Loan> findByBookId(Long bookId);

    @Query("""
        SELECT l FROM Loan l
        JOIN FETCH l.book b
        JOIN FETCH b.author a
        JOIN FETCH l.member m
        WHERE l.status = :status
        AND l.dueDate < :today
        ORDER BY l.dueDate ASC
        """)
    List<Loan> findOverdueLoansWithDetails(
        @Param("today") LocalDate today,
        @Param("status") LoanStatus status
    );

    @Query("""
        SELECT l FROM Loan l
        JOIN FETCH l.book b
        JOIN FETCH b.author a
        JOIN FETCH l.member m
        WHERE a.id = :authorId
        AND l.status = :status
        ORDER BY l.loanDate DESC
        """)
    List<Loan> findLoansByAuthorAndStatus(
        @Param("authorId") Long authorId,
        @Param("status") LoanStatus status
    );
}