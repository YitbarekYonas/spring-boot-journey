package com.JavaBackEnd.spring_boot_journey_week4_day2.repository;

import com.JavaBackEnd.spring_boot_journey_week4_day2.entity.Loan;
import com.JavaBackEnd.spring_boot_journey_week4_day2.entity.LoanStatus;
import com.JavaBackEnd.spring_boot_journey_week4_day2.projection.LoanCountByBook;
import com.JavaBackEnd.spring_boot_journey_week4_day2.projection.MemberLoanStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    // ── Derived queries ────────────────────────────────────────────────────

    List<Loan> findByStatus(LoanStatus status);
    List<Loan> findByMemberId(Long memberId);
    List<Loan> findByBookId(Long bookId);
    long countByStatus(LoanStatus status);

    // ── Native queries with interface projections ─────────────────────────

    // ✅ CORRECT: Interface projection - no DTO class!
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
    List<LoanCountByBook> getLoanCountPerBook();

    // ✅ CORRECT: Another interface projection
    @Query(value = """
        SELECT
            m.id            AS memberId,
            m.name          AS memberName,
            m.email         AS memberEmail,
            COUNT(l.id)     AS totalLoans,
            SUM(CASE WHEN l.status = 'ACTIVE'   THEN 1 ELSE 0 END) AS activeLoans,
            SUM(CASE WHEN l.status = 'OVERDUE'  THEN 1 ELSE 0 END) AS overdueLoans,
            SUM(CASE WHEN l.status = 'RETURNED' THEN 1 ELSE 0 END) AS returnedLoans
        FROM members m
        LEFT JOIN loans l ON l.member_id = m.id
        WHERE m.is_active = true
        GROUP BY m.id, m.name, m.email
        ORDER BY totalLoans DESC
        """,
        nativeQuery = true)
    List<MemberLoanStats> getMemberLoanStatistics();

    // ✅ Bulk update - @Modifying with native query
    @Modifying
    @Query(value = """
        UPDATE loans
        SET status = 'OVERDUE'
        WHERE status = 'ACTIVE'
        AND due_date < CURRENT_DATE
        """,
        nativeQuery = true)
    int bulkMarkOverdue();
}