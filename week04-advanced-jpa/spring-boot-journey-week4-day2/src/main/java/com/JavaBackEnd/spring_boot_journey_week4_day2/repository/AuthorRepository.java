package com.JavaBackEnd.spring_boot_journey_week4_day2.repository;

import com.JavaBackEnd.spring_boot_journey_week4_day2.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    // ── Derived query methods ───────────────────────────────────────────────

    Optional<Author> findByEmail(String email);

    List<Author> findByLastNameContainingIgnoreCase(String lastName);

    boolean existsByEmail(String email);

    // ── JPQL Queries ────────────────────────────────────────────────────────

    // Find author with all their books loaded (JOIN FETCH)
    @Query("""
        SELECT DISTINCT a FROM Author a
        LEFT JOIN FETCH a.books
        WHERE a.id = :id
        """)
    Optional<Author> findByIdWithBooks(@Param("id") Long id);

    // Authors who have published at least one book
    @Query("""
        SELECT a FROM Author a
        WHERE SIZE(a.books) > 0
        ORDER BY a.lastName ASC
        """)
    List<Author> findAuthorsWithBooks();

    // Authors with minimum number of books
    @Query("""
        SELECT a FROM Author a
        WHERE SIZE(a.books) >= :minBooks
        ORDER BY SIZE(a.books) DESC
        """)
    List<Author> findAuthorsWithMinBooks(@Param("minBooks") int minBooks);

    // Search authors by name (first or last)
    @Query("""
        SELECT a FROM Author a
        WHERE LOWER(a.firstName) LIKE LOWER(CONCAT('%', :name, '%'))
        OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :name, '%'))
        ORDER BY a.lastName ASC, a.firstName ASC
        """)
    List<Author> searchByName(@Param("name") String name);

    // Count books per author
    @Query("""
        SELECT a.id, a.firstName, a.lastName, SIZE(a.books)
        FROM Author a
        ORDER BY SIZE(a.books) DESC
        """)
    List<Object[]> countBooksPerAuthor();

    // Find authors by book genre (uses the relationship)
    @Query("""
        SELECT DISTINCT a FROM Author a
        JOIN a.books b
        WHERE LOWER(b.genre) = LOWER(:genre)
        ORDER BY a.lastName ASC
        """)
    List<Author> findAuthorsByBookGenre(@Param("genre") String genre);
}