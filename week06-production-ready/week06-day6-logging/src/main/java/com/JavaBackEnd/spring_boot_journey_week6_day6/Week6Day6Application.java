package com.JavaBackEnd.spring_boot_journey_week6_day6;

import com.JavaBackEnd.spring_boot_journey_week6_day6.entity.Book;
import com.JavaBackEnd.spring_boot_journey_week6_day6.entity.User;
import com.JavaBackEnd.spring_boot_journey_week6_day6.repository.BookRepository;
import com.JavaBackEnd.spring_boot_journey_week6_day6.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

// ── What to observe when app starts ───────────────────────────────────────
// Watch the console carefully. You'll see:
//   1. Spring Boot banner
//   2. Bean initialization logs (INFO level from Spring)
//   3. Our seed data logs: "✅ Seed data inserted"
//   4. The log pattern includes [requestId] — it's empty on startup
//      because there's no HTTP request yet. That's correct.
//   5. When you hit an endpoint in Postman, watch the filter logs appear:
//        → GET /api/books
//        ... service debug logs ...
//        ← GET /api/books → 200 (12ms)
//      All lines for that request share the same [requestId] prefix.
@SpringBootApplication
@Slf4j
public class Week6Day6Application {

    public static void main(String[] args) {
        SpringApplication.run(Week6Day6Application.class, args);
    }

    @Bean
    CommandLineRunner seedData(UserRepository userRepo,
                               BookRepository bookRepo,
                               PasswordEncoder encoder) {
        return args -> {
            log.info("Seeding database...");

            userRepo.save(User.builder()
                    .name("Alice Admin").email("admin@library.com")
                    .password(encoder.encode("password123")).role("ADMIN").build());

            userRepo.save(User.builder()
                    .name("Bob Member").email("bob@library.com")
                    .password(encoder.encode("password123")).role("USER").build());

            bookRepo.save(Book.builder()
                    .title("Clean Code").isbn("978-0132350884")
                    .genre("Software Engineering").authorName("Robert C. Martin")
                    .price(new BigDecimal("38.99")).totalCopies(5).availableCopies(3).build());

            bookRepo.save(Book.builder()
                    .title("Effective Java").isbn("978-0134685991")
                    .genre("Java Programming").authorName("Joshua Bloch")
                    .price(new BigDecimal("49.99")).totalCopies(3).availableCopies(3).build());

            bookRepo.save(Book.builder()
                    .title("Spring Boot in Action").isbn("978-1617292545")
                    .genre("Spring Framework").authorName("Craig Walls")
                    .price(new BigDecimal("44.99")).totalCopies(4).availableCopies(0).build());

            log.info("✅ Seed data inserted — 2 users, 3 books");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log.info("Postman test endpoints:");
            log.info("  GET  http://localhost:8080/api/books               ← watch filter logs");
            log.info("  GET  http://localhost:8080/api/books/1             ← debug log in service");
            log.info("  GET  http://localhost:8080/api/books/99            ← warn: not found");
            log.info("  GET  http://localhost:8080/api/books/1/sync        ← info: sync ok");
            log.info("  GET  http://localhost:8080/api/books/2/sync        ← error: sync failed");
            log.info("  POST http://localhost:8080/api/books               ← info: book created");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        };
    }
}
