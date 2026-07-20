package com.JavaBackEnd.library.controller;

import com.JavaBackEnd.library.dto.BookSummaryDto;
import com.JavaBackEnd.library.dto.PageResponse;
import com.JavaBackEnd.library.entity.Book;
import com.JavaBackEnd.library.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<PageResponse<Book>> getAllBooks(
            @PageableDefault(size = 10, sort = "title",
                             direction = Sort.Direction.ASC)
            Pageable pageable) {

        Page<Book> page = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(new PageResponse<>(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<Book>> searchBooks(
            @RequestParam String keyword,
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {

        Page<Book> page = bookService.searchBooks(keyword, pageable);
        return ResponseEntity.ok(new PageResponse<>(page));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<PageResponse<Book>> getByGenre(
            @PathVariable String genre,
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {

        Page<Book> page = bookService.getBooksByGenre(genre, pageable);
        return ResponseEntity.ok(new PageResponse<>(page));
    }

    @GetMapping("/available")
    public ResponseEntity<List<BookSummaryDto>> getAvailableBooks() {
        return ResponseEntity.ok(bookService.getAvailableBookSummaries());
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