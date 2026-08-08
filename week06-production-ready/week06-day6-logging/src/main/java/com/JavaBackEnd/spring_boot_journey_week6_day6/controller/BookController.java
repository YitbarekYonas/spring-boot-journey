package com.JavaBackEnd.spring_boot_journey_week6_day6.controller;

import com.JavaBackEnd.spring_boot_journey_week6_day6.dto.request.CreateBookRequest;
import com.JavaBackEnd.spring_boot_journey_week6_day6.dto.response.BookResponse;
import com.JavaBackEnd.spring_boot_journey_week6_day6.dto.response.BookSummaryResponse;
import com.JavaBackEnd.spring_boot_journey_week6_day6.entity.Book;
import com.JavaBackEnd.spring_boot_journey_week6_day6.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

// ── How much to log in controllers? ───────────────────────────────────────
// Less than you think. The RequestLoggingFilter already logs:
//   → POST /api/books (request in)
//   ← POST /api/books → 201 (200ms) (response out)
//
// So controllers only need to log things the filter CAN'T see:
//   - Which path variables / params were used (filter only sees the URI pattern)
//   - Validation failures
//   - Business decisions (chose this branch, skipped that branch)
//
// Service layer logs the actual business events (book created, not found, etc.)
// Don't double-log. One clear log per event is enough.
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<Page<BookSummaryResponse>> getAllBooks(
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {

        // Controller log: what params were resolved (not visible in filter)
        log.debug("GET /api/books — page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());

        Page<Book> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(books.map(BookSummaryResponse::from));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        log.debug("GET /api/books/{}", id);

        return bookService.getBookById(id)
                .map(BookResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        // Service already logged warn when not found — no need to log again here
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BookSummaryResponse>> search(
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable) {

        log.debug("GET /api/books/search — keyword='{}'", keyword);
        Page<Book> books = bookService.searchBooks(keyword, pageable);
        return ResponseEntity.ok(books.map(BookSummaryResponse::from));
    }

    @GetMapping("/available")
    public ResponseEntity<Page<BookSummaryResponse>> getAvailableBooks(
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(
            bookService.getAvailableBooks(pageable).map(BookSummaryResponse::from)
        );
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(
            @Valid @RequestBody CreateBookRequest request,
            UriComponentsBuilder uriBuilder) {

        log.debug("POST /api/books — title='{}', isbn='{}'", request.getTitle(), request.getIsbn());

        Book created = bookService.createBook(request);
        BookResponse response = BookResponse.from(created);

        URI location = uriBuilder
                .path("/api/books/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
        // bookService.createBook() already logged: "Book created — id=X, title='Y'"
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        log.debug("DELETE /api/books/{}", id);
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // ── Demo endpoint to show log.error() ─────────────────────────────────
    // GET /api/books/{id}/sync — triggers the error logging demo
    // Try with id=2 (even) to see error, id=1 (odd) to see success
    @GetMapping("/{id}/sync")
    public ResponseEntity<String> syncBook(@PathVariable Long id) {
        log.info("Manual sync triggered for book id={}", id);
        try {
            bookService.simulateSystemFailure(id);
            return ResponseEntity.ok("Sync successful for book " + id);
        } catch (Exception ex) {
            // Controller only re-catches if it needs to change the HTTP response.
            // The actual error was already logged by the service.
            // Don't log.error() here too — that would print the same error twice.
            return ResponseEntity.internalServerError()
                    .body("Sync failed: " + ex.getMessage());
        }
    }
}
