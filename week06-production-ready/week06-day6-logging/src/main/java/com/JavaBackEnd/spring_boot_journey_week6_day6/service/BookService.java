package com.JavaBackEnd.spring_boot_journey_week6_day6.service;

import com.JavaBackEnd.spring_boot_journey_week6_day6.dto.request.CreateBookRequest;
import com.JavaBackEnd.spring_boot_journey_week6_day6.entity.Book;
import com.JavaBackEnd.spring_boot_journey_week6_day6.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// ── Logging Strategy — which level goes where ─────────────────────────────
//
// log.debug()  → Method entry/exit, variable values, internal flow detail.
//                These are invisible in production (level=INFO). They're for
//                you during development when something is wrong and you need
//                to trace exactly what happened.
//                "What is the service doing right now?"
//
// log.info()   → Meaningful business events that always matter.
//                "A book was created." "A user registered." "Checkout completed."
//                Should be readable as an audit trail by a non-developer.
//                "What happened in the system?"
//
// log.warn()   → Something unexpected happened but the system handled it.
//                Request for non-existent resource. Retried operation. Config fallback.
//                No action needed NOW, but worth monitoring for patterns.
//                "Something looks odd — should I investigate?"
//
// log.error()  → Something broke and we couldn't recover.
//                Always investigate these. Usually paired with an exception.
//                "Stop what you're doing and look at this."
//
// Common mistake: log.error() for 404 Not Found.
//   Not found is expected behavior — the caller asked for something that doesn't exist.
//   That's a log.warn() at most. Reserve log.error() for system-level failures.
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    public Page<Book> getAllBooks(Pageable pageable) {
        // DEBUG — internal flow detail, only visible during development
        log.debug("getAllBooks() called — page={}, size={}, sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        Page<Book> result = bookRepository.findAll(pageable);

        // DEBUG — result detail, useful when debugging pagination issues
        log.debug("getAllBooks() returning {} books (total: {})",
                result.getNumberOfElements(),
                result.getTotalElements());

        return result;
    }

    public Optional<Book> getBookById(Long id) {
        log.debug("getBookById() called — id={}", id);

        Optional<Book> book = bookRepository.findById(id);

        // WARN — not found is expected, not a system failure
        // Don't use log.error() here — 404 is normal behavior
        if (book.isEmpty()) {
            log.warn("getBookById() — book not found: id={}", id);
        } else {
            log.debug("getBookById() — found: {}", book.get().getTitle());
        }

        return book;
    }

    public Page<Book> searchBooks(String keyword, Pageable pageable) {
        log.debug("searchBooks() called — keyword='{}', page={}",
                keyword, pageable.getPageNumber());

        // WARN — empty keyword is valid but unusual, worth noting
        if (keyword == null || keyword.isBlank()) {
            log.warn("searchBooks() called with blank keyword — returning all books");
            return bookRepository.findAll(pageable);
        }

        Page<Book> result = bookRepository.searchByKeyword(keyword, pageable);
        log.debug("searchBooks() — keyword='{}' returned {} results", keyword, result.getTotalElements());
        return result;
    }

    public Page<Book> getAvailableBooks(Pageable pageable) {
        log.debug("getAvailableBooks() called");
        return bookRepository.findByAvailableCopiesGreaterThan(0, pageable);
    }

    @Transactional
    public Book createBook(CreateBookRequest request) {
        log.debug("createBook() called — title='{}', isbn='{}'",
                request.getTitle(), request.getIsbn());

        // WARN — duplicate is a handled business rule, not a system error
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            log.warn("createBook() — duplicate ISBN rejected: '{}'", request.getIsbn());
            throw new IllegalArgumentException(
                "Book with ISBN " + request.getIsbn() + " already exists");
        }

        Book book = Book.builder()
                .title(request.getTitle())
                .isbn(request.getIsbn())
                .genre(request.getGenre())
                .authorName(request.getAuthorName())
                .price(request.getPrice())
                .totalCopies(request.getTotalCopies())
                .availableCopies(request.getTotalCopies())
                .build();

        Book saved = bookRepository.save(book);

        // INFO — a book was created. This is a meaningful business event.
        // Info log reads like an audit trail: what happened, key identifiers.
        log.info("Book created — id={}, title='{}', isbn='{}'",
                saved.getId(), saved.getTitle(), saved.getIsbn());

        return saved;
    }

    @Transactional
    public void deleteBook(Long id) {
        log.debug("deleteBook() called — id={}", id);

        if (!bookRepository.existsById(id)) {
            log.warn("deleteBook() — book not found: id={}", id);
            throw new IllegalArgumentException("Book not found: " + id);
        }

        bookRepository.deleteById(id);

        // INFO — deletion is a meaningful, irreversible business event
        log.info("Book deleted — id={}", id);
    }

    // ── Example: when to use log.error() ──────────────────────────────────
    // This method simulates an external payment/integration call that can fail
    // at the system level (network failure, third-party API down, etc.)
    public void simulateSystemFailure(Long bookId) {
        log.info("Starting external sync for book id={}", bookId);
        try {
            // Imagine this calls an external inventory system
            if (bookId % 2 == 0) throw new RuntimeException("External API timeout");
            log.info("External sync completed — book id={}", bookId);
        } catch (Exception ex) {
            // ERROR — system couldn't recover, needs investigation
            // Always log the exception object (ex) as the last parameter
            // so Logback prints the full stack trace
            log.error("External sync FAILED for book id={} — {}", bookId, ex.getMessage(), ex);
            throw ex;
        }
    }
}
