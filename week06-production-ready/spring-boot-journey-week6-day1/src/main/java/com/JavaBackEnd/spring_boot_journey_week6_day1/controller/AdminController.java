package com.JavaBackEnd.spring_boot_journey_week6_day1.controller;

import com.JavaBackEnd.spring_boot_journey_week6_day1.entity.User;
import com.JavaBackEnd.spring_boot_journey_week6_day1.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PatchMapping("/users/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> disableUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setEnabled(false);
                    userRepository.save(user);
                    return ResponseEntity.ok(
                        Map.of("message", "User disabled: " + user.getEmail())
                    );
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/users/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> enableUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setEnabled(true);
                    userRepository.save(user);
                    return ResponseEntity.ok(
                        Map.of("message", "User enabled: " + user.getEmail())
                    );
                })
                .orElse(ResponseEntity.notFound().build());
    }
}