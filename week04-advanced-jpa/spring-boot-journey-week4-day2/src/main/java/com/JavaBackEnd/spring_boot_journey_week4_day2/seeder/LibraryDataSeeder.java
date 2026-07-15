package com.JavaBackEnd.spring_boot_journey_week4_day2.seeder;

import com.JavaBackEnd.spring_boot_journey_week4_day2.entity.*;
import com.JavaBackEnd.spring_boot_journey_week4_day2.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class LibraryDataSeeder {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;

    public LibraryDataSeeder(AuthorRepository authorRepository,
                             BookRepository bookRepository,
                             MemberRepository memberRepository,
                             LoanRepository loanRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
    }

    @PostConstruct
    @Transactional
    public void seed() {
        if (authorRepository.count() > 0) {
            return;
        }

        System.out.println("\n🌱 Seeding library data...");

        Author martin = new Author(
            "Robert", "Martin",
            "robert.martin@example.com",
            LocalDate.of(1952, 12, 5)
        );
        martin.setBiography("Author of Clean Code and The Clean Coder");
        authorRepository.save(martin);

        Author fowler = new Author(
            "Martin", "Fowler",
            "martin.fowler@example.com",
            LocalDate.of(1963, 12, 18)
        );
        fowler.setBiography("Author of Refactoring");
        authorRepository.save(fowler);

        Book cleanCode = new Book(
            "Clean Code: A Handbook of Agile Software Craftsmanship",
            "978-0132350884",
            LocalDate.of(2008, 8, 1),
            new BigDecimal("38.99"),
            3,
            "Software Engineering"
        );
        martin.addBook(cleanCode);
        bookRepository.save(cleanCode);

        Book cleanCoder = new Book(
            "The Clean Coder",
            "978-0137081073",
            LocalDate.of(2011, 5, 13),
            new BigDecimal("34.99"),
            2,
            "Software Engineering"
        );
        martin.addBook(cleanCoder);
        bookRepository.save(cleanCoder);

        Book refactoring = new Book(
            "Refactoring: Improving the Design of Existing Code",
            "978-0201485677",
            LocalDate.of(1999, 7, 8),
            new BigDecimal("49.99"),
            2,
            "Software Engineering"
        );
        fowler.addBook(refactoring);
        bookRepository.save(refactoring);

        Member alice = new Member(
            "Alice Johnson",
            "alice@example.com",
            "+1-555-0101"
        );
        memberRepository.save(alice);

        Member bob = new Member(
            "Bob Smith",
            "bob@example.com",
            "+1-555-0102"
        );
        memberRepository.save(bob);

        // Active Loan
        cleanCode.checkout();
        bookRepository.save(cleanCode);
        Loan activeLoan = new Loan(cleanCode, alice, 14);
        loanRepository.save(activeLoan);

        // Overdue Loan
        refactoring.checkout();
        bookRepository.save(refactoring);
        Loan overdueLoan = new Loan(refactoring, bob, 14);
        loanRepository.save(overdueLoan);

        System.out.println("✅ Library data seeded!");
        System.out.println("   Authors: " + authorRepository.count());
        System.out.println("   Books: " + bookRepository.count());
        System.out.println("   Members: " + memberRepository.count());
        System.out.println("   Loans: " + loanRepository.count());
    }
}