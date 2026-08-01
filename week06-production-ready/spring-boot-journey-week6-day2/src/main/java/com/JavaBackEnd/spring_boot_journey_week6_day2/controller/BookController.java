package com.JavaBackEnd.spring_boot_journey_week6_day2.controller;

import com.JavaBackEnd.spring_boot_journey_week6_day2.entity.Book;
import com.JavaBackEnd.spring_boot_journey_week6_day2.exception.BusinessRuleException;
import com.JavaBackEnd.spring_boot_journey_week6_day2.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody @Valid Book book) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookService.createBook(book));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // For testing BusinessRuleException
    @PostMapping("/checkout")
    public ResponseEntity<String> checkoutBook() {
        throw BusinessRuleException.bookNotAvailable();
    }
}