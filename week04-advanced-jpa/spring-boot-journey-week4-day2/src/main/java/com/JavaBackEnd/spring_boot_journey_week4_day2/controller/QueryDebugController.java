package com.JavaBackEnd.spring_boot_journey_week4_day2.controller;

import com.JavaBackEnd.spring_boot_journey_week4_day2.entity.Book;
import com.JavaBackEnd.spring_boot_journey_week4_day2.projection.LoanCountByBook;
import com.JavaBackEnd.spring_boot_journey_week4_day2.projection.MemberLoanStats;
import com.JavaBackEnd.spring_boot_journey_week4_day2.repository.BookRepository;
import com.JavaBackEnd.spring_boot_journey_week4_day2.repository.LoanRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/debug")
public class QueryDebugController {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    public QueryDebugController(LoanRepository loanRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
    }

    // ── Native Query Endpoints ──────────────────────────────────────────────

    @GetMapping("/loans/stats/per-book")
    public List<LoanCountByBook> getLoanCountPerBook() {
        return loanRepository.getLoanCountPerBook();
    }

    @GetMapping("/loans/stats/per-member")
    public List<MemberLoanStats> getMemberLoanStatistics() {
        return loanRepository.getMemberLoanStatistics();
    }

    // ── JPQL Book Query Endpoints ───────────────────────────────────────────

    @GetMapping("/books/search")
    public List<Book> searchBooks(@RequestParam String keyword) {
        return bookRepository.searchByTitleOrAuthor(keyword);
    }

    @GetMapping("/books/genre/{genre}")
    public List<Book> booksByGenre(@PathVariable String genre) {
        return bookRepository.findByGenreAndAvailabilityAndPriceRange(
            genre, 0, new BigDecimal("999999.99")
        );
    }

    @GetMapping("/books/author/{lastName}")
    public List<Book> booksByAuthor(@PathVariable String lastName) {
        return bookRepository.findAvailableBooksByAuthorLastName(lastName);
    }

    // ── JPQL Stats Endpoints ────────────────────────────────────────────────

    @GetMapping("/books/stats/genre-counts")
    public List<Object[]> genreCounts() {
        return bookRepository.countBooksPerGenre();
    }
}