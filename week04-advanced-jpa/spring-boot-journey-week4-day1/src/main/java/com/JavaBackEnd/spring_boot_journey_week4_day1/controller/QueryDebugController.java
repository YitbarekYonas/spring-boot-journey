package com.JavaBackEnd.spring_boot_journey_week4_day1.controller;

import com.JavaBackEnd.spring_boot_journey_week4_day1.entity.Book;
import com.JavaBackEnd.spring_boot_journey_week4_day1.entity.Loan;
import com.JavaBackEnd.spring_boot_journey_week4_day1.entity.LoanStatus;
import com.JavaBackEnd.spring_boot_journey_week4_day1.repository.BookRepository;
import com.JavaBackEnd.spring_boot_journey_week4_day1.repository.LoanRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/debug")
public class QueryDebugController {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public QueryDebugController(BookRepository bookRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
    }

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

    @GetMapping("/books/stats/genre-counts")
    public List<Object[]> genreCounts() {
        return bookRepository.countBooksPerGenre();
    }

    @GetMapping("/books/on-loan")
    public List<Book> booksOnLoan() {
        return bookRepository.findBooksCurrentlyOnLoan();
    }

    @GetMapping("/books/price-range")
    public List<Book> booksByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        return bookRepository.findByPriceRange(min, max);
    }

    @GetMapping("/books/stats/avg-price/{genre}")
    public Double avgPriceByGenre(@PathVariable String genre) {
        return bookRepository.averagePriceByGenre(genre);
    }

    @GetMapping("/loans/overdue-with-details")
    public List<Loan> overdueLoans() {
        return loanRepository.findOverdueLoansWithDetails(
            LocalDate.now(), LoanStatus.ACTIVE
        );
    }

    @GetMapping("/loans/by-author/{authorId}")
    public List<Loan> loansByAuthor(@PathVariable Long authorId) {
        return loanRepository.findLoansByAuthorAndStatus(
            authorId, LoanStatus.ACTIVE
        );
    }
}