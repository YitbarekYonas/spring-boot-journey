package com.JavaBackEnd.spring_boot_journey_week6_day3.service.impl;

import com.JavaBackEnd.spring_boot_journey_week6_day3.dto.ChangePasswordRequest;
import com.JavaBackEnd.spring_boot_journey_week6_day3.dto.LoginRequest;
import com.JavaBackEnd.spring_boot_journey_week6_day3.dto.RegisterRequest;
import com.JavaBackEnd.spring_boot_journey_week6_day3.entity.User;
import com.JavaBackEnd.spring_boot_journey_week6_day3.entity.UserRole;
import com.JavaBackEnd.spring_boot_journey_week6_day3.exception.DuplicateResourceException;
import com.JavaBackEnd.spring_boot_journey_week6_day3.repository.UserRepository;
import com.JavaBackEnd.spring_boot_journey_week6_day3.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ Constructor injection - Spring will inject both beans
    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw DuplicateResourceException.email(request.getEmail());
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(
            request.getName(),
            request.getEmail(),
            hashedPassword,
            UserRole.USER
        );

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User login(LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}