package com.JavaBackEnd.spring_boot_journey_week4_day2.repository;

import com.JavaBackEnd.spring_boot_journey_week4_day2.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // ── JPQL Query 1: Multi-condition WHERE ─────────────────────────────────

    @Query("""
        SELECT b FROM Book b
        JOIN FETCH b.author a
        WHERE b.genre = :genre
        AND b.availableCopies > :minAvailable
        AND b.price <= :maxPrice
        ORDER BY b.title ASC
        """)
    List<Book> findByGenreAndAvailabilityAndPriceRange(
        @Param("genre") String genre,
        @Param("minAvailable") int minAvailable,
        @Param("maxPrice") BigDecimal maxPrice
    );

    // ── JPQL Query 2: JOIN with filter on joined entity ────────────────────

    @Query("""
        SELECT b FROM Book b
        JOIN FETCH b.author a
        WHERE a.lastName = :authorLastName
        AND b.availableCopies > 0
        ORDER BY b.title ASC
        """)
    List<Book> findAvailableBooksByAuthorLastName(
        @Param("authorLastName") String authorLastName
    );

    // ── JPQL Query 3: String LIKE search ────────────────────────────────────

    @Query("""
        SELECT b FROM Book b
        JOIN FETCH b.author a
        WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(a.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY b.title ASC
        """)
    List<Book> searchByTitleOrAuthor(@Param("keyword") String keyword);

    // ── JPQL Query 4: Aggregate COUNT ───────────────────────────────────────

    @Query("""
        SELECT b.genre, COUNT(b)
        FROM Book b
        GROUP BY b.genre
        ORDER BY COUNT(b) DESC
        """)
    List<Object[]> countBooksPerGenre();
}