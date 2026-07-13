package com.JavaBackEnd.library.repository;

import com.JavaBackEnd.library.entity.Loan;
import com.JavaBackEnd.library.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByStatus(LoanStatus status);

    List<Loan> findByMemberId(Long memberId);

    List<Loan> findByBookId(Long bookId);

    List<Loan> findByMemberIdAndStatus(Long memberId, LoanStatus status);

    boolean existsByBookIdAndMemberIdAndStatus(
        Long bookId, Long memberId, LoanStatus status);

    long countByStatus(LoanStatus status);

    @Query("""
        SELECT l FROM Loan l
        JOIN FETCH l.book b
        JOIN FETCH l.member m
        WHERE l.status = 'ACTIVE'
        AND l.dueDate < :today
        ORDER BY l.dueDate ASC
        """)
    List<Loan> findOverdueLoans(@Param("today") LocalDate today);

    @Query("""
        SELECT l FROM Loan l
        JOIN FETCH l.book b
        JOIN FETCH b.author
        WHERE l.member.id = :memberId
        ORDER BY l.loanDate DESC
        """)
    List<Loan> findByMemberIdWithBookAndAuthor(@Param("memberId") Long memberId);

    @Query("""
        SELECT l FROM Loan l
        WHERE l.book.id = :bookId
        AND l.member.id = :memberId
        AND l.status IN ('ACTIVE', 'OVERDUE')
        """)
    Optional<Loan> findActiveLoanForBookAndMember(
        @Param("bookId") Long bookId,
        @Param("memberId") Long memberId);
}