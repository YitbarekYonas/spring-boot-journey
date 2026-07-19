package com.JavaBackEnd.spring_boot_journey_week4_day6.repository;

import com.JavaBackEnd.spring_boot_journey_week4_day6.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // ❌ N+1 Problem - Keep for demonstration
    List<Book> findAll();

    // ✅ JOIN FETCH - Fixes N+1 for list
    @Query("""
        SELECT b FROM Book b
        JOIN FETCH b.author a
        ORDER BY b.title ASC
        """)
    List<Book> findAllWithAuthor();

    // ✅ JOIN FETCH for single entity - Fixes PUT
    @Query("""
        SELECT b FROM Book b
        JOIN FETCH b.author a
        WHERE b.id = :id
        """)
    Optional<Book> findByIdWithAuthor(Long id);
}