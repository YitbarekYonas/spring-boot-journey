package com.JavaBackEnd.library.repository;

import com.JavaBackEnd.library.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    Optional<Author> findByEmail(String email);

    List<Author> findByLastNameContainingIgnoreCase(String lastName);

    boolean existsByEmail(String email);

    @Query("""
        SELECT DISTINCT a FROM Author a
        LEFT JOIN FETCH a.books
        WHERE a.id = :id
        """)
    Optional<Author> findByIdWithBooks(@Param("id") Long id);

    @Query("""
        SELECT a FROM Author a
        WHERE SIZE(a.books) > 0
        ORDER BY a.lastName ASC
        """)
    List<Author> findAuthorsWithBooks();
}