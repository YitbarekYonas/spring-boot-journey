package com.JavaBackEnd.spring_boot_journey_week5_day2.seeder;

import com.JavaBackEnd.spring_boot_journey_week5_day2.entity.Book;
import com.JavaBackEnd.spring_boot_journey_week5_day2.entity.User;
import com.JavaBackEnd.spring_boot_journey_week5_day2.entity.UserRole;
import com.JavaBackEnd.spring_boot_journey_week5_day2.repository.BookRepository;
import com.JavaBackEnd.spring_boot_journey_week5_day2.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      BookRepository bookRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Transactional
    public void seed() {
        seedUsers();
        seedBooks();
    }

    private void seedUsers() {
        if (userRepository.count() > 0) {
            return;
        }

        System.out.println("\n🌱 Seeding users...");

        userRepository.save(new User(
            "Admin User",
            "admin@library.com",
            passwordEncoder.encode("admin123"),
            UserRole.ADMIN
        ));

        userRepository.save(new User(
            "Jane Librarian",
            "jane@library.com",
            passwordEncoder.encode("lib123"),
            UserRole.LIBRARIAN
        ));

        userRepository.save(new User(
            "John Member",
            "john@library.com",
            passwordEncoder.encode("mem123"),
            UserRole.MEMBER
        ));

        System.out.println("✅ Seeded " + userRepository.count() + " users.");
    }

    private void seedBooks() {
        if (bookRepository.count() > 0) {
            return;
        }

        System.out.println("🌱 Seeding books...");

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