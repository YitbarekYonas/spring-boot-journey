package com.JavaBackEnd.library.service.impl;

import com.JavaBackEnd.library.dto.BookSummaryDto;
import com.JavaBackEnd.library.entity.Author;
import com.JavaBackEnd.library.entity.Book;
import com.JavaBackEnd.library.repository.AuthorRepository;
import com.JavaBackEnd.library.repository.BookRepository;
import com.JavaBackEnd.library.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookServiceImpl(BookRepository bookRepository,
                           AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @Override
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAllWithAuthor(pageable);
    }

    @Override
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findByIdWithAuthor(id);
    }

    @Override
    public Page<Book> searchBooks(String keyword, Pageable pageable) {
        return bookRepository.searchPaginated(keyword, pageable);
    }

    @Override
    public Page<Book> getBooksByGenre(String genre, Pageable pageable) {
        return bookRepository.findByGenreWithAuthor(genre, pageable);
    }

    @Override
    public List<BookSummaryDto> getAvailableBookSummaries() {
        return bookRepository.findAvailableBookSummaries();
    }

    @Override
    @Transactional
    public Book createBook(Book book, Long authorId) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new IllegalArgumentException(
                "Book with ISBN already exists: " + book.getIsbn());
        }
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Author not found: " + authorId));
        author.addBook(book);
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Optional<Book> updateBook(Long id, Book updatedBook) {
        return bookRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(updatedBook.getTitle());
                    existing.setPrice(updatedBook.getPrice());
                    existing.setGenre(updatedBook.getGenre());
                    existing.setTotalCopies(updatedBook.getTotalCopies());
                    return bookRepository.save(existing);
                });
    }
}