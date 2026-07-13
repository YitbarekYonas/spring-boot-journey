package com.JavaBackEnd.library.service;

import com.JavaBackEnd.library.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {

    List<Book> getAllBooks();

    Optional<Book> getBookById(Long id);

    List<Book> searchBooks(String keyword);

    List<Book> getAvailableBooks();

    List<Book> getBooksByGenre(String genre);

    Book createBook(Book book, Long authorId);

    Optional<Book> updateBook(Long id, Book updatedBook);
}