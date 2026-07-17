package com.JavaBackEnd.spring_boot_journey_week4_day4.controller;

import com.JavaBackEnd.spring_boot_journey_week4_day4.dto.BookSummaryDto;
import com.JavaBackEnd.spring_boot_journey_week4_day4.entity.Book;
import com.JavaBackEnd.spring_boot_journey_week4_day4.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // ✅ Full entity with JOIN FETCH - now works!
    @GetMapping("/full")
    public ResponseEntity<List<Book>> getAllBooksFull() {
        return ResponseEntity.ok(bookRepository.findAllWithAuthor());
    }

    // DTO - SELECT only needed columns
    @GetMapping("/summary")
    public ResponseEntity<List<BookSummaryDto>> getBookSummaries() {
        return ResponseEntity.ok(bookRepository.findAvailableBookSummaries());
    }

    // Paginated DTO
    @GetMapping
    public ResponseEntity<Page<BookSummaryDto>> getAllBooks(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(bookRepository.findAllBookSummaries(pageable));
    }
}