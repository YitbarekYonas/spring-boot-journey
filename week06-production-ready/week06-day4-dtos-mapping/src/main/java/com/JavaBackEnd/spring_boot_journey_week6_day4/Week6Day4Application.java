package com.JavaBackEnd.spring_boot_journey_week6_day4;

import com.JavaBackEnd.spring_boot_journey_week6_day4.entity.Book;
import com.JavaBackEnd.spring_boot_journey_week6_day4.entity.User;
import com.JavaBackEnd.spring_boot_journey_week6_day4.repository.BookRepository;
import com.JavaBackEnd.spring_boot_journey_week6_day4.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@SpringBootApplication
@Slf4j
public class Week6Day4Application {

    public static void main(String[] args) {
        SpringApplication.run(Week6Day4Application.class, args);
    }

    // Seeds some data when the app starts so you can test immediately in Postman
    @Bean
    CommandLineRunner seedData(UserRepository userRepo,
                               BookRepository bookRepo,
                               PasswordEncoder encoder) {
        return args -> {
            // Create two users — notice password is stored hashed
            User admin = User.builder()
                    .name("Alice Admin")
                    .email("admin@library.com")
                    .password(encoder.encode("password123"))   // BCrypt hash
                    .role("ADMIN")
                    .enabled(true)
                    .build();

            User member = User.builder()
                    .name("Bob Member")
                    .email("bob@library.com")
                    .password(encoder.encode("password123"))
                    .role("USER")
                    .enabled(true)
                    .build();

            userRepo.save(admin);
            userRepo.save(member);

            // Create some books
            bookRepo.save(Book.builder()
                    .title("Clean Code")
                    .isbn("978-0132350884")
                    .genre("Software Engineering")
                    .authorName("Robert C. Martin")
                    .price(new BigDecimal("38.99"))
                    .totalCopies(5)
                    .availableCopies(3)
                    .build());

            bookRepo.save(Book.builder()
                    .title("Effective Java")
                    .isbn("978-0134685991")
                    .genre("Java Programming")
                    .authorName("Joshua Bloch")
                    .price(new BigDecimal("49.99"))
                    .totalCopies(3)
                    .availableCopies(3)
                    .build());

            bookRepo.save(Book.builder()
                    .title("Spring Boot in Action")
                    .isbn("978-1617292545")
                    .genre("Spring Framework")
                    .authorName("Craig Walls")
                    .price(new BigDecimal("44.99"))
                    .totalCopies(4)
                    .availableCopies(0)   // all copies on loan — available = false
                    .build());

            log.info("✅ Seed data inserted — 2 users, 3 books");
            log.info("Test endpoints:");
            log.info("  GET  http://localhost:8080/api/books");
            log.info("  GET  http://localhost:8080/api/books/1");
            log.info("  POST http://localhost:8080/api/books");
            log.info("  GET  http://localhost:8080/api/users       (WRONG - returns entity)");
            log.info("  GET  http://localhost:8080/api/users/safe  (RIGHT - returns DTO)");
        };
    }
}
