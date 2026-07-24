package com.JavaBackEnd.spring_boot_journey_week5_day3.service.impl;

import com.JavaBackEnd.spring_boot_journey_week5_day3.dto.*;
import com.JavaBackEnd.spring_boot_journey_week5_day3.entity.User;
import com.JavaBackEnd.spring_boot_journey_week5_day3.entity.UserRole;
import com.JavaBackEnd.spring_boot_journey_week5_day3.repository.UserRepository;
import com.JavaBackEnd.spring_boot_journey_week5_day3.security.CustomUserDetails;
import com.JavaBackEnd.spring_boot_journey_week5_day3.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                "Email already registered: " + request.getEmail());
        }

        validatePassword(request.getPassword());

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
            request.getName(),
            request.getEmail(),
            hashedPassword,
            UserRole.MEMBER
        );

        User saved = userRepository.save(user);

        return new RegisterResponse(
            saved.getId(),
            saved.getName(),
            saved.getEmail(),
            saved.getRole()
        );
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails =
            (CustomUserDetails) authentication.getPrincipal();

        return new LoginResponse(
            userDetails.getUsername(),
            userDetails.getUser().getRole()
        );
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "User not found: " + userId));

        if (!passwordEncoder.matches(
                request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException(
                "Current password is incorrect");
        }

        validatePassword(request.getNewPassword());

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException(
                "New password and confirmation do not match");
        }

        if (passwordEncoder.matches(
                request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException(
                "New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                "Password must be at least 8 characters");
        }
        if (password.length() > 72) {
            throw new IllegalArgumentException(
                "Password must not exceed 72 characters");
        }
    }
}