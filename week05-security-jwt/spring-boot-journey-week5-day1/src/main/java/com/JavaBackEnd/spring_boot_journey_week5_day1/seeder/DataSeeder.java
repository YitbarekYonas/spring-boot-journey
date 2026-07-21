package com.JavaBackEnd.spring_boot_journey_week5_day1.seeder;

import com.JavaBackEnd.spring_boot_journey_week5_day1.entity.Book;
import com.JavaBackEnd.spring_boot_journey_week5_day1.repository.BookRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder {

    private final BookRepository bookRepository;

    public DataSeeder(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @PostConstruct
    @Transactional
    public void seed() {
        if (bookRepository.count() > 0) {
            return;
        }

        System.out.println("\n🌱 Seeding books...");

        bookRepository.save(new Book(
            "Clean Code: A Handbook of Agile Software Craftsmanship",
            "978-0132350884",
            "Software Engineering"
        ));

        bookRepository.save(new Book(
            "The Clean Coder: A Code of Conduct for Professional Programmers",
            "978-0137081073",
            "Software Engineering"
        ));

        bookRepository.save(new Book(
            "Refactoring: Improving the Design of Existing Code",
            "978-0201485677",
            "Software Engineering"
        ));

        System.out.println("✅ Seeded " + bookRepository.count() + " books.");
    }
}