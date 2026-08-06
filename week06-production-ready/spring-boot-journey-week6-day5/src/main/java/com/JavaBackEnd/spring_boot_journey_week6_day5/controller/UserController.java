package com.JavaBackEnd.controller;

import com.JavaBackEnd.dto.request.UserRequest;
import com.JavaBackEnd.dto.response.UserResponse;
import com.JavaBackEnd.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor  // Generates constructor for final fields
@Slf4j  // Adds 'log' field
public class UserController {
    
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        log.info("Received request to create user");
        UserResponse response = userService.createUser(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        log.debug("Received request for user ID: {}", id);
        UserResponse response = userService.getUser(id);
        return ResponseEntity.ok(response);
    }
}