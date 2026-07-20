package com.JavaBackEnd.library.service;

import com.JavaBackEnd.library.dto.BookSummaryDto;
import com.JavaBackEnd.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BookService {

    Page<Book> getAllBooks(Pageable pageable);

    Optional<Book> getBookById(Long id);

    Page<Book> searchBooks(String keyword, Pageable pageable);

    Page<Book> getBooksByGenre(String genre, Pageable pageable);

    List<BookSummaryDto> getAvailableBookSummaries();

    Book createBook(Book book, Long authorId);

    Optional<Book> updateBook(Long id, Book updatedBook);
}