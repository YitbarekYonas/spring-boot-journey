package com.JavaBackEnd.spring_boot_journey_week6_day4.controller;

import com.JavaBackEnd.spring_boot_journey_week6_day4.dto.response.UserResponse;
import com.JavaBackEnd.spring_boot_journey_week6_day4.entity.User;
import com.JavaBackEnd.spring_boot_journey_week6_day4.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// ── Purpose of This Controller ────────────────────────────────────────────
// This file exists ONLY to demonstrate the WRONG vs RIGHT pattern.
// It has two endpoints for the same data:
//
//   GET /api/users         → WRONG: returns raw User entity
//                             ← password hash visible in response!
//   GET /api/users/safe    → RIGHT: returns UserResponse DTO
//                             ← password excluded, 6 clean fields
//
// Hit both in Postman and compare the JSON. That comparison IS the lesson.
//
// In a real project: you'd only have the /safe version and delete the /wrong one.
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // ── WRONG — returns raw entity ─────────────────────────────────────────
    // Hit: GET http://localhost:8080/api/users
    //
    // You'll see password hash, security flags, audit fields —
    // all things the client should NEVER receive.
    @GetMapping
    public ResponseEntity<List<User>> getAllUsersWrong() {
        // Jackson serializes the entire User entity to JSON.
        // password: "$2a$10$N9qo8..." is now visible to anyone who calls this API.
        return ResponseEntity.ok(userRepository.findAll());
    }

    // ── RIGHT — returns DTO ────────────────────────────────────────────────
    // Hit: GET http://localhost:8080/api/users/safe
    //
    // You'll see exactly: id, name, email, role, enabled, createdAt.
    // No password. No security flags. No audit metadata.
    @GetMapping("/safe")
    public ResponseEntity<List<UserResponse>> getAllUsersSafe() {
        List<User> users = userRepository.findAll();

        // Map each entity to a safe DTO before sending
        List<UserResponse> response = users.stream()
                .map(UserResponse::from)   // UserResponse.from(user) for each user
                .toList();

        return ResponseEntity.ok(response);
    }

    // ── Single user — WRONG (entity) vs RIGHT (DTO) ───────────────────────
    @GetMapping("/{id}/raw")
    public ResponseEntity<User> getUserRaw(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserSafe(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(UserResponse::from)   // ← entity → safe DTO
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
