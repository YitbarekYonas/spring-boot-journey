package com.JavaBackEnd.spring_boot_journey_week6_day3.seeder;

import com.JavaBackEnd.spring_boot_journey_week6_day3.entity.User;
import com.JavaBackEnd.spring_boot_journey_week6_day3.entity.UserRole;
import com.JavaBackEnd.spring_boot_journey_week6_day3.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ Constructor injection
    public DataSeeder(UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Transactional
    public void seed() {
        if (userRepository.count() > 0) return;

        System.out.println("\n🌱 Seeding users...");

        User admin = new User(
            "Admin User",
            "admin@example.com",
            passwordEncoder.encode("admin123"),
            UserRole.ADMIN
        );
        userRepository.save(admin);

        System.out.println("✅ Seeded " + userRepository.count() + " user.");
        System.out.println("  admin@example.com / admin123 (ADMIN)");
    }
}