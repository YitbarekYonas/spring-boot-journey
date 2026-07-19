package com.JavaBackEnd.spring_boot_journey_week4_day6.controller;

import com.JavaBackEnd.spring_boot_journey_week4_day6.entity.Book;
import com.JavaBackEnd.spring_boot_journey_week4_day6.repository.BookRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // ✅ JOIN FETCH - No N+1
    @GetMapping
    public ResponseEntity<List<Book>> getBooksWithAuthor() {
        return ResponseEntity.ok(bookRepository.findAllWithAuthor());
    }

    // ❌ N+1 Problem - Keep for demonstration (still works but shows N+1 in logs)
    @GetMapping("/n-plus-one")
    public ResponseEntity<List<Book>> getBooksNPlusOne() {
        return ResponseEntity.ok(bookRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Book> createBook(
            @RequestBody Book book,
            UriComponentsBuilder uriBuilder) {

        Book created = bookRepository.save(book);
        // ✅ created_at, updated_at, created_by, last_modified_by set automatically!

        URI location = uriBuilder
                .path("/api/books/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    // ✅ Fixed PUT - Uses findByIdWithAuthor with JOIN FETCH
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(
            @PathVariable Long id,
            @RequestBody Book book) {

        return bookRepository.findByIdWithAuthor(id)
                .map(existing -> {
                    existing.setTitle(book.getTitle());
                    existing.setIsbn(book.getIsbn());
                    // ✅ Author is already loaded with JOIN FETCH
                    return ResponseEntity.ok(bookRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}