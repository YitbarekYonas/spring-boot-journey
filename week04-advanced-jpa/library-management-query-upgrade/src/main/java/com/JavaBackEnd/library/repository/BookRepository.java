package com.JavaBackEnd.library.repository;

import com.JavaBackEnd.library.dto.BookSummaryDto;
import com.JavaBackEnd.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
    List<Book> findByAuthorId(Long authorId);

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
    Page<Book> searchPaginated(
        @Param("keyword") String keyword,
        Pageable pageable
    );

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

    @Query("""
        SELECT new com.JavaBackEnd.library.dto.BookSummaryDto(
            b.id,
            b.title,
            b.isbn,
            b.genre,
            b.availableCopies,
            b.price,
            a.firstName,
            a.lastName,
            b.publishDate
        )
        FROM Book b
        JOIN b.author a
        WHERE b.availableCopies > 0
        ORDER BY b.title ASC
        """)
    List<BookSummaryDto> findAvailableBookSummaries();

    @Query(
        value = """
            SELECT b FROM Book b
            JOIN FETCH b.author a
            """,
        countQuery = "SELECT COUNT(b) FROM Book b"
    )
    Page<Book> findAllWithAuthor(Pageable pageable);

    @Query("""
        SELECT b FROM Book b
        JOIN FETCH b.author a
        WHERE b.id = :id
        """)
    Optional<Book> findByIdWithAuthor(@Param("id") Long id);
}