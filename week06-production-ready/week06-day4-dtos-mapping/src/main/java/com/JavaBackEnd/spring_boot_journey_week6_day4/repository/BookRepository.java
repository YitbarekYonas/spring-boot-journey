package com.JavaBackEnd.spring_boot_journey_week6_day4.repository;

import com.JavaBackEnd.spring_boot_journey_week6_day4.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Derived query — Spring generates SQL from method name
    Page<Book> findByGenre(String genre, Pageable pageable);

    // JPQL — case-insensitive title/author search
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.authorName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Book> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Only available books
    Page<Book> findByAvailableCopiesGreaterThan(int minCopies, Pageable pageable);

    boolean existsByIsbn(String isbn);
}
