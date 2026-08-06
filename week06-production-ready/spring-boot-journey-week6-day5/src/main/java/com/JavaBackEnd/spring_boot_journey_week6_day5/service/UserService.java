package com.JavaBackEnd.service;

import com.JavaBackEnd.dto.request.UserRequest;
import com.JavaBackEnd.dto.response.UserResponse;
import com.JavaBackEnd.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor  // Generates constructor for final fields
@Slf4j  // Adds 'log' field
public class UserService {
    
    private final List<User> users = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserResponse createUser(UserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());
        
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role("USER")
                .build();
        
        // Simulating ID generation (in real app, DB would do this)
        try {
            var idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, idGenerator.getAndIncrement());
        } catch (Exception e) {
            log.error("Error setting ID", e);
        }
        
        users.add(user);
        log.info("User created with ID: {}", user.getId());
        
        return UserResponse.from(user);
    }

    public UserResponse getUser(Long id) {
        log.debug("Fetching user with ID: {}", id);
        
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .map(UserResponse::from)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", id);
                    return new RuntimeException("User not found");
                });
    }
}