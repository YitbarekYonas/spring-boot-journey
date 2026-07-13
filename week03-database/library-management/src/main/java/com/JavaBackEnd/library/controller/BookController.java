package com.JavaBackEnd.library.controller;

import com.JavaBackEnd.library.entity.Book;
import com.JavaBackEnd.library.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
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
    public ResponseEntity<Book> getById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> search(
            @RequestParam String keyword) {
        return ResponseEntity.ok(bookService.searchBooks(keyword));
    }

    @GetMapping("/available")
    public ResponseEntity<List<Book>> getAvailable() {
        return ResponseEntity.ok(bookService.getAvailableBooks());
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Book>> getByGenre(
            @PathVariable String genre) {
        return ResponseEntity.ok(bookService.getBooksByGenre(genre));
    }

    @PostMapping
    public ResponseEntity<Book> createBook(
            @RequestBody Book book,
            @RequestParam Long authorId,
            UriComponentsBuilder uriBuilder) {

        Book created = bookService.createBook(book, authorId);
        URI location = uriBuilder
                .path("/api/books/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(
            @PathVariable Long id,
            @RequestBody Book book) {
        return bookService.updateBook(id, book)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}