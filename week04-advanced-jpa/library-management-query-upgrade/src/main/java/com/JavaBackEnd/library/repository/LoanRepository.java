package com.JavaBackEnd.library.repository;

import com.JavaBackEnd.library.dto.BookLoanStats;
import com.JavaBackEnd.library.dto.LoanDetailDto;
import com.JavaBackEnd.library.entity.Loan;
import com.JavaBackEnd.library.entity.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    long countByStatus(LoanStatus status);
    boolean existsByBookIdAndMemberIdAndStatus(
        Long bookId, Long memberId, LoanStatus status);

    @Query("""
        SELECT l FROM Loan l
        JOIN FETCH l.book b
        JOIN FETCH b.author a
        JOIN FETCH l.member m
        WHERE l.status = 'ACTIVE'
        AND l.dueDate < :today
        ORDER BY l.dueDate ASC
        """)
    List<Loan> findOverdueLoans(@Param("today") LocalDate today);

    @Query("""
        SELECT new com.JavaBackEnd.library.dto.LoanDetailDto(
            l.id,
            l.status,
            l.loanDate,
            l.dueDate,
            l.returnDate,
            b.title,
            b.isbn,
            a.firstName,
            a.lastName,
            m.name,
            m.email
        )
        FROM Loan l
        JOIN l.book b
        JOIN b.author a
        JOIN l.member m
        WHERE l.member.id = :memberId
        ORDER BY l.loanDate DESC
        """)
    List<LoanDetailDto> findLoanDetailsByMemberId(
        @Param("memberId") Long memberId);

    @Query("""
        SELECT l FROM Loan l
        WHERE l.book.id = :bookId
        AND l.member.id = :memberId
        AND l.status IN ('ACTIVE', 'OVERDUE')
        """)
    Optional<Loan> findActiveLoanForBookAndMember(
        @Param("bookId") Long bookId,
        @Param("memberId") Long memberId);

    @Query(
        value = """
            SELECT l FROM Loan l
            JOIN FETCH l.book b
            JOIN FETCH b.author a
            JOIN FETCH l.member m
            WHERE l.status = :status
            """,
        countQuery = """
            SELECT COUNT(l) FROM Loan l
            WHERE l.status = :status
            """
    )
    Page<Loan> findByStatusWithDetails(
        @Param("status") LoanStatus status,
        Pageable pageable
    );

    @Query(value = """
        SELECT
            b.id                AS bookId,
            b.title             AS bookTitle,
            b.isbn              AS bookIsbn,
            CONCAT(a.first_name, ' ', a.last_name) AS authorName,
            COUNT(l.id)         AS totalLoans,
            SUM(CASE WHEN l.status = 'ACTIVE'   THEN 1 ELSE 0 END) AS activeLoans,
            SUM(CASE WHEN l.status = 'RETURNED' THEN 1 ELSE 0 END) AS returnedLoans
        FROM books b
        JOIN authors a ON a.id = b.author_id
        LEFT JOIN loans l ON l.book_id = b.id
        GROUP BY b.id, b.title, b.isbn, a.first_name, a.last_name
        ORDER BY totalLoans DESC
        """,
        nativeQuery = true)
    List<BookLoanStats> getBookLoanStatistics();
}