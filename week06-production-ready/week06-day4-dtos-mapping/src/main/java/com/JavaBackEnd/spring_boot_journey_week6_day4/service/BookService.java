package com.JavaBackEnd.spring_boot_journey_week6_day4.service;

import com.JavaBackEnd.spring_boot_journey_week6_day4.dto.request.CreateBookRequest;
import com.JavaBackEnd.spring_boot_journey_week6_day4.entity.Book;
import com.JavaBackEnd.spring_boot_journey_week6_day4.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// ── Key Design Decision ────────────────────────────────────────────────────
// BookService returns ENTITIES (Book), NOT DTOs (BookResponse).
//
// Why?
// The service is the "domain layer" — it works with domain objects.
// If the service returned BookResponse, it would be coupled to the
// HTTP response format. What if you need the same service logic from
// a CLI command, a scheduled job, or a test? They'd receive an HTTP
// response DTO that they don't need.
//
// Services return domain objects. Controllers translate to HTTP representation.
// That's the clean separation.
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)  // all methods read-only by default
public class BookService {

    private final BookRepository bookRepository;

    public Page<Book> getAllBooks(Pageable pageable) {
        log.debug("Fetching all books, page: {}", pageable.getPageNumber());
        return bookRepository.findAll(pageable);
    }

    public Optional<Book> getBookById(Long id) {
        log.debug("Fetching book with id: {}", id);
        return bookRepository.findById(id);
    }

    public Page<Book> searchBooks(String keyword, Pageable pageable) {
        return bookRepository.searchByKeyword(keyword, pageable);
    }

    public Page<Book> getAvailableBooks(Pageable pageable) {
        return bookRepository.findByAvailableCopiesGreaterThan(0, pageable);
    }

    @Transactional   // overrides the class-level readOnly = true for writes
    public Book createBook(CreateBookRequest request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
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
        log.info("Created book: {} (id={})", saved.getTitle(), saved.getId());
        return saved;
        // Returns Book entity — NOT BookResponse
        // The controller will call BookResponse.from(saved) to map it
    }

    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Book not found: " + id);
        }
        bookRepository.deleteById(id);
        log.info("Deleted book id: {}", id);
    }
}
