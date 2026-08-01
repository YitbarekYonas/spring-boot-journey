package com.JavaBackEnd.spring_boot_journey_week6_day2.service;

import com.JavaBackEnd.spring_boot_journey_week6_day2.entity.Book;
import com.JavaBackEnd.spring_boot_journey_week6_day2.exception.DuplicateResourceException;
import com.JavaBackEnd.spring_boot_journey_week6_day2.exception.ResourceNotFoundException;
import com.JavaBackEnd.spring_boot_journey_week6_day2.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.book(id));
    }

    @Transactional
    public Book createBook(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw DuplicateResourceException.isbn(book.getIsbn());
        }
        return bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = getBookById(id);
        bookRepository.delete(book);
    }
}