package com.JavaBackEnd.spring_boot_journey_week4_day3.controller;

import com.JavaBackEnd.spring_boot_journey_week4_day3.entity.Book;
import com.JavaBackEnd.spring_boot_journey_week4_day3.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // GET /api/books?page=0&size=10&sort=title,asc
    @GetMapping
    public ResponseEntity<Page<Book>> getAllBooks(
            @PageableDefault(size = 10, sort = "title", direction = Sort.Direction.ASC)
            Pageable pageable) {

        // Cap page size
        int size = pageable.getPageSize();
        if (size > 100) {
            pageable = PageRequest.of(pageable.getPageNumber(), 100, pageable.getSort());
        }

        // Whitelist sort fields
        Set<String> allowedSortFields = Set.of("title", "publishDate", "price", "genre", "availableCopies");
        String sortProperty = pageable.getSort().stream()
                .findFirst()
                .map(Sort.Order::getProperty)
                .orElse("title");

        if (!allowedSortFields.contains(sortProperty)) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("title"));
        }

        // ✅ Use JOIN FETCH version
        Page<Book> page = bookRepository.findAllWithAuthor(pageable);
        return ResponseEntity.ok(page);
    }

    // GET /api/books/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Book> getById(@PathVariable Long id) {
        Book book = bookRepository.findByIdWithAuthor(id);
        if (book != null) {
            return ResponseEntity.ok(book);
        }
        return ResponseEntity.notFound().build();
    }

    // GET /api/books/available?page=0&size=5
    @GetMapping("/available")
    public ResponseEntity<Page<Book>> getAvailableBooks(
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {

        Page<Book> page = bookRepository.findAvailableBooksWithAuthor(pageable);
        return ResponseEntity.ok(page);
    }

    // GET /api/books/search?keyword=clean&page=0&size=5
    @GetMapping("/search")
    public ResponseEntity<Page<Book>> searchBooks(
            @RequestParam String keyword,
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {

        Page<Book> page = bookRepository.searchByTitleOrAuthorPaginated(keyword, pageable);
        return ResponseEntity.ok(page);
    }

    // GET /api/books/genre/{genre}?page=0&size=5
    @GetMapping("/genre/{genre}")
    public ResponseEntity<Page<Book>> getByGenre(
            @PathVariable String genre,
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {

        Page<Book> page = bookRepository.findByGenreWithAuthor(genre, pageable);
        return ResponseEntity.ok(page);
    }
}