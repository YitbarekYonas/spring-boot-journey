package com.JavaBackEnd.spring_boot_journey_week4_day5.seeder;

import com.JavaBackEnd.spring_boot_journey_week4_day5.entity.BookBranch;
import com.JavaBackEnd.spring_boot_journey_week4_day5.repository.BookBranchRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder {

    private final BookBranchRepository bookBranchRepository;

    public DataSeeder(BookBranchRepository bookBranchRepository) {
        this.bookBranchRepository = bookBranchRepository;
    }

    @PostConstruct
    @Transactional
    public void seed() {
        if (bookBranchRepository.count() > 0) {
            return;
        }

        System.out.println("\n🌱 Seeding branch data...");

        bookBranchRepository.save(new BookBranch("Central", "Clean Code", 5));
        bookBranchRepository.save(new BookBranch("East", "Clean Code", 3));
        bookBranchRepository.save(new BookBranch("West", "Clean Code", 0));

        System.out.println("✅ Branches seeded:");
        bookBranchRepository.findAll().forEach(System.out::println);
    }
}