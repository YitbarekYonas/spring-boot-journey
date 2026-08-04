package com.JavaBackEnd.spring_boot_journey_week6_day4.controller;

import com.JavaBackEnd.spring_boot_journey_week6_day4.dto.request.CreateBookRequest;
import com.JavaBackEnd.spring_boot_journey_week6_day4.dto.response.BookResponse;
import com.JavaBackEnd.spring_boot_journey_week6_day4.dto.response.BookSummaryResponse;
import com.JavaBackEnd.spring_boot_journey_week6_day4.entity.Book;
import com.JavaBackEnd.spring_boot_journey_week6_day4.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

// ── What the Controller's Job Is ──────────────────────────────────────────
// 1. Accept HTTP requests
// 2. Validate input (@Valid)
// 3. Call the service (which returns domain entities)
// 4. MAP entities → DTOs         ← THIS is today's lesson
// 5. Return ResponseEntity with correct status code
//
// The controller translates between the HTTP world (DTOs, status codes)
// and the domain world (entities, service methods).
// It has NO business logic — that stays in BookService.
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // ── GET /api/books ────────────────────────────────────────────────────
    // Returns Page<BookSummaryResponse> — compact DTO for list views
    // Page.map(BookSummaryResponse::from) transforms Page<Book> → Page<BookSummaryResponse>
    // while preserving all pagination metadata (totalElements, totalPages, etc.)
    @GetMapping
    public ResponseEntity<Page<BookSummaryResponse>> getAllBooks(
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {

        Page<Book> books = bookService.getAllBooks(pageable);

        // Page.map() — transforms content while keeping pagination metadata intact
        Page<BookSummaryResponse> response = books.map(BookSummaryResponse::from);
        //                                              ↑ method reference = BookSummaryResponse.from(book)

        return ResponseEntity.ok(response);
    }

    // ── GET /api/books/{id} ───────────────────────────────────────────────
    // Returns full BookResponse for the detail view
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {

        return bookService.getBookById(id)
                .map(BookResponse::from)        // entity → full DTO
                .map(ResponseEntity::ok)         // DTO → 200 OK response
                .orElse(ResponseEntity.notFound().build());  // 404 if not found
    }

    // ── GET /api/books/search?keyword=clean ───────────────────────────────
    @GetMapping("/search")
    public ResponseEntity<Page<BookSummaryResponse>> search(
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<Book> books = bookService.searchBooks(keyword, pageable);
        return ResponseEntity.ok(books.map(BookSummaryResponse::from));
    }

    // ── GET /api/books/available ──────────────────────────────────────────
    @GetMapping("/available")
    public ResponseEntity<Page<BookSummaryResponse>> getAvailableBooks(
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(
            bookService.getAvailableBooks(pageable).map(BookSummaryResponse::from)
        );
    }

    // ── POST /api/books ───────────────────────────────────────────────────
    // @Valid triggers bean validation on CreateBookRequest before method runs
    // Returns 201 Created with Location header pointing to the new resource
    @PostMapping
    public ResponseEntity<BookResponse> createBook(
            @Valid @RequestBody CreateBookRequest request,
            UriComponentsBuilder uriBuilder) {

        Book created = bookService.createBook(request);  // service returns entity
        BookResponse response = BookResponse.from(created);  // controller maps to DTO

        URI location = uriBuilder
                .path("/api/books/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
        // 201 Created + Location: /api/books/4 + body: BookResponse JSON
    }

    // ── DELETE /api/books/{id} ────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();  // 204 No Content
    }
}
