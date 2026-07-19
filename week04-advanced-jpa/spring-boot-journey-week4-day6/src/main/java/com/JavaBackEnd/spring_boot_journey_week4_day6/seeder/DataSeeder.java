package com.JavaBackEnd.spring_boot_journey_week4_day6.seeder;

import com.JavaBackEnd.spring_boot_journey_week4_day6.entity.Author;
import com.JavaBackEnd.spring_boot_journey_week4_day6.entity.Book;
import com.JavaBackEnd.spring_boot_journey_week4_day6.repository.BookRepository;
import com.JavaBackEnd.spring_boot_journey_week4_day6.repository.AuthorRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public DataSeeder(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @PostConstruct
    @Transactional
    public void seed() {
        if (authorRepository.count() > 0) {
            return;
        }

        System.out.println("\n🌱 Seeding data...");

        Author martin = authorRepository.save(
            new Author("Robert", "Martin", "robert@example.com")
        );

        Author fowler = authorRepository.save(
            new Author("Martin", "Fowler", "martin@example.com")
        );

        bookRepository.save(new Book("Clean Code", "978-0132350884", martin));
        bookRepository.save(new Book("The Clean Coder", "978-0137081073", martin));
        bookRepository.save(new Book("Refactoring", "978-0201485677", fowler));

        System.out.println("✅ Seeded " + authorRepository.count() + " authors and " + bookRepository.count() + " books.");
        System.out.println("\n📋 Audit fields will be set automatically on save!");
    }
}