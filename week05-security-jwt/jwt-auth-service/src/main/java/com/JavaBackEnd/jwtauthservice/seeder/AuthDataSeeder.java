package com.JavaBackEnd.jwtauthservice.seeder;

import com.JavaBackEnd.jwtauthservice.entity.User;
import com.JavaBackEnd.jwtauthservice.entity.UserRole;
import com.JavaBackEnd.jwtauthservice.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AuthDataSeeder {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthDataSeeder(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Transactional
    public void seed() {
        if (userRepository.count() > 0) return;

        System.out.println("\n🌱 Seeding auth service users...");

        userRepository.save(new User(
            "System Admin",
            "admin@authservice.com",
            passwordEncoder.encode("Admin@123"),
            UserRole.ADMIN
        ));

        userRepository.save(new User(
            "Alice User",
            "alice@authservice.com",
            passwordEncoder.encode("Alice@123"),
            UserRole.USER
        ));

        userRepository.save(new User(
            "Bob User",
            "bob@authservice.com",
            passwordEncoder.encode("Bob@123"),
            UserRole.USER
        ));

        System.out.println("✅ Seeded " + userRepository.count() + " users.");
    }
}