package com.JavaBackEnd.library.repository;

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

    List<Book> findByGenre(String genre);

    List<Book> findByGenreIgnoreCase(String genre);

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByAvailableCopiesGreaterThan(int minCopies);

    List<Book> findByAvailableCopiesGreaterThanEqual(int minAvailable);

    boolean existsByIsbn(String isbn);

    List<Book> findByAuthorId(Long authorId);

    List<Book> findByAuthorLastNameContainingIgnoreCase(String authorLastName);

    @Query("""
        SELECT b FROM Book b
        JOIN FETCH b.author
        WHERE b.id = :id
        """)
    Optional<Book> findByIdWithAuthor(@Param("id") Long id);

    @Query("""
        SELECT b FROM Book b
        JOIN FETCH b.author
        ORDER BY b.title ASC
        """)
    List<Book> findAllWithAuthor();

    @Query("""
        SELECT b FROM Book b
        JOIN FETCH b.author
        WHERE b.availableCopies > 0
        ORDER BY b.title ASC
        """)
    List<Book> findAvailableBooksWithAuthor();

    @Query("""
        SELECT b FROM Book b
        JOIN FETCH b.author a
        WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(a.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY b.title ASC
        """)
    List<Book> searchByTitleOrAuthor(@Param("keyword") String keyword);

    Page<Book> findByGenreIgnoreCase(String genre, Pageable pageable);
}