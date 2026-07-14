package com.JavaBackEnd.spring_boot_journey_week4_day1.repository;

import com.JavaBackEnd.spring_boot_journey_week4_day1.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
    List<Book> findByAuthorId(Long authorId);

    // ── JPQL Query 1: Multi-condition WHERE ─────────────────────────────────

    // Predicted SQL: SELECT b.*, a.* FROM books b
    // INNER JOIN authors a ON a.id = b.author_id
    // WHERE b.genre = ? AND b.available_copies > ? AND b.price <= ?
    // ORDER BY b.title ASC
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

    // Predicted SQL: SELECT b.*, a.* FROM books b
    // INNER JOIN authors a ON a.id = b.author_id
    // WHERE a.last_name = ? AND b.available_copies > 0
    // ORDER BY b.title ASC
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

    // Predicted SQL: SELECT b.*, a.* FROM books b
    // INNER JOIN authors a ON a.id = b.author_id
    // WHERE LOWER(b.title) LIKE LOWER('%'||?||'%')
    // OR LOWER(a.first_name) LIKE LOWER('%'||?||'%')
    // OR LOWER(a.last_name) LIKE LOWER('%'||?||'%')
    // ORDER BY b.title ASC
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

    // Predicted SQL: SELECT COUNT(*) FROM books WHERE genre = ?
    @Query("SELECT COUNT(b) FROM Book b WHERE b.genre = :genre")
    long countByGenre(@Param("genre") String genre);

    // ── Additional JPQL queries for debugging ───────────────────────────────

    @Query("""
        SELECT b FROM Book b
        JOIN FETCH b.author a
        WHERE b.price BETWEEN :minPrice AND :maxPrice
        ORDER BY b.price ASC
        """)
    List<Book> findByPriceRange(
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice
    );

    @Query("""
        SELECT DISTINCT b FROM Book b
        JOIN FETCH b.author a
        JOIN b.loans l
        WHERE l.status = 'ACTIVE'
        ORDER BY b.title ASC
        """)
    List<Book> findBooksCurrentlyOnLoan();

    @Query("""
        SELECT b.genre, COUNT(b)
        FROM Book b
        GROUP BY b.genre
        ORDER BY COUNT(b) DESC
        """)
    List<Object[]> countBooksPerGenre();

    @Query("SELECT AVG(b.price) FROM Book b WHERE b.genre = :genre")
    Double averagePriceByGenre(@Param("genre") String genre);
}