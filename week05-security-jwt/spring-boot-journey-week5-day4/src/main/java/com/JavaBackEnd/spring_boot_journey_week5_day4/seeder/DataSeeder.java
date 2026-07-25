package com.JavaBackEnd.spring_boot_journey_week5_day4.seeder;

import com.JavaBackEnd.spring_boot_journey_week5_day4.entity.User;
import com.JavaBackEnd.spring_boot_journey_week5_day4.entity.UserRole;
import com.JavaBackEnd.spring_boot_journey_week5_day4.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Transactional
    public void seed() {
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
}