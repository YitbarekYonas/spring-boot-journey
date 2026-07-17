package com.JavaBackEnd.spring_boot_journey_week4_day4.repository;

import com.JavaBackEnd.spring_boot_journey_week4_day4.dto.BookSummaryDto;
import com.JavaBackEnd.spring_boot_journey_week4_day4.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // ✅ Full entity with JOIN FETCH - fixes LazyInitializationException
    @Query("""
        SELECT b FROM Book b
        JOIN FETCH b.author a
        ORDER BY b.title ASC
        """)
    List<Book> findAllWithAuthor();

    // Full entity - plain findAll (causes LazyInitializationException)
    // Remove or comment out this method
    // List<Book> findAll();

    // DTO Query - SELECT only 7 columns
    @Query("""
        SELECT new com.JavaBackEnd.spring_boot_journey_week4_day4.dto.BookSummaryDto(
            b.id,
            b.title,
            b.genre,
            b.availableCopies,
            b.price,
            a.firstName,
            a.lastName
        )
        FROM Book b
        JOIN b.author a
        WHERE b.availableCopies > 0
        ORDER BY b.title ASC
        """)
    List<BookSummaryDto> findAvailableBookSummaries();

    // Paginated DTO Query
    @Query(
        value = """
            SELECT new com.JavaBackEnd.spring_boot_journey_week4_day4.dto.BookSummaryDto(
                b.id,
                b.title,
                b.genre,
                b.availableCopies,
                b.price,
                a.firstName,
                a.lastName
            )
            FROM Book b
            JOIN b.author a
            """,
        countQuery = """
            SELECT COUNT(b) FROM Book b
            """
    )
    Page<BookSummaryDto> findAllBookSummaries(Pageable pageable);
}