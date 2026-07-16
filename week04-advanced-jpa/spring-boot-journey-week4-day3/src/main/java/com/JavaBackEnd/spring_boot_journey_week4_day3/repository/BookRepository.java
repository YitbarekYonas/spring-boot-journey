package com.JavaBackEnd.spring_boot_journey_week4_day3.repository;

import com.JavaBackEnd.spring_boot_journey_week4_day3.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // ✅ Fix: Use JOIN FETCH for findAll with pagination
    @Query(
        value = """
            SELECT b FROM Book b
            JOIN FETCH b.author a
            """,
        countQuery = """
            SELECT COUNT(b) FROM Book b
            """
    )
    Page<Book> findAllWithAuthor(Pageable pageable);

    // ✅ Fix: Use JOIN FETCH for findById
    @Query("""
        SELECT b FROM Book b
        JOIN FETCH b.author a
        WHERE b.id = :id
        """)
    Book findByIdWithAuthor(@Param("id") Long id);

    // ✅ Fix: Use JOIN FETCH for available books
    @Query(
        value = """
            SELECT b FROM Book b
            JOIN FETCH b.author a
            WHERE b.availableCopies > 0
            """,
        countQuery = """
            SELECT COUNT(b) FROM Book b
            WHERE b.availableCopies > 0
            """
    )
    Page<Book> findAvailableBooksWithAuthor(Pageable pageable);

    // ✅ Fix: Use JOIN FETCH for search
    @Query(
        value = """
            SELECT b FROM Book b
            JOIN FETCH b.author a
            WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(a.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """,
        countQuery = """
            SELECT COUNT(b) FROM Book b
            JOIN b.author a
            WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(a.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """
    )
    Page<Book> searchByTitleOrAuthorPaginated(
        @Param("keyword") String keyword,
        Pageable pageable
    );

    // ✅ Fix: Use JOIN FETCH for genre filter
    @Query(
        value = """
            SELECT b FROM Book b
            JOIN FETCH b.author a
            WHERE LOWER(b.genre) = LOWER(:genre)
            """,
        countQuery = """
            SELECT COUNT(b) FROM Book b
            WHERE LOWER(b.genre) = LOWER(:genre)
            """
    )
    Page<Book> findByGenreWithAuthor(
        @Param("genre") String genre,
        Pageable pageable
    );
}