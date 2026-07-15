package com.JavaBackEnd.spring_boot_journey_week4_day2.repository;

import com.JavaBackEnd.spring_boot_journey_week4_day2.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // ── Derived query methods ───────────────────────────────────────────────

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Member> findByActive(boolean active);

    List<Member> findByNameContainingIgnoreCase(String name);

    long countByActive(boolean active);

    // ── JPQL Queries ────────────────────────────────────────────────────────

    // Members with their loans loaded (JOIN FETCH)
    @Query("""
        SELECT DISTINCT m FROM Member m
        LEFT JOIN FETCH m.loans l
        LEFT JOIN FETCH l.book b
        LEFT JOIN FETCH b.author a
        WHERE m.id = :id
        """)
    Optional<Member> findByIdWithLoans(@Param("id") Long id);

    // Members with active loans only
    @Query("""
        SELECT DISTINCT m FROM Member m
        JOIN m.loans l
        WHERE l.status = 'ACTIVE'
        ORDER BY m.name ASC
        """)
    List<Member> findMembersWithActiveLoans();

    // Members with overdue loans
    @Query("""
        SELECT DISTINCT m FROM Member m
        JOIN m.loans l
        WHERE l.status = 'OVERDUE' OR (l.status = 'ACTIVE' AND l.dueDate < CURRENT_DATE)
        ORDER BY m.name ASC
        """)
    List<Member> findMembersWithOverdueLoans();

    // Members with no loans (IS EMPTY)
    @Query("""
        SELECT m FROM Member m
        WHERE m.loans IS EMPTY
        ORDER BY m.name ASC
        """)
    List<Member> findMembersWithNoLoans();

    // Search members by name or email
    @Query("""
        SELECT m FROM Member m
        WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(m.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY m.name ASC
        """)
    List<Member> searchByNameOrEmail(@Param("keyword") String keyword);

    // Active members with active loan count
    @Query("""
        SELECT m.id, m.name, m.email, COUNT(l)
        FROM Member m
        LEFT JOIN m.loans l
        WHERE m.active = true
        GROUP BY m.id, m.name, m.email
        ORDER BY COUNT(l) DESC
        """)
    List<Object[]> getActiveMembersWithLoanCounts();

    // Members who joined in a specific year
    @Query("""
        SELECT m FROM Member m
        WHERE YEAR(m.membershipDate) = :year
        ORDER BY m.membershipDate DESC
        """)
    List<Member> findMembersByMembershipYear(@Param("year") int year);

    // Members with phone number starting with a prefix
    @Query("""
        SELECT m FROM Member m
        WHERE m.phoneNumber IS NOT NULL
        AND m.phoneNumber LIKE CONCAT(:prefix, '%')
        ORDER BY m.name ASC
        """)
    List<Member> findMembersByPhonePrefix(@Param("prefix") String prefix);
}