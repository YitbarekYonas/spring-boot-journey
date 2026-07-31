package com.JavaBackEnd.spring_boot_journey_week6_day1.service.impl;

import com.JavaBackEnd.spring_boot_journey_week6_day1.config.JwtProperties;
import com.JavaBackEnd.spring_boot_journey_week6_day1.dto.LoginRequest;
import com.JavaBackEnd.spring_boot_journey_week6_day1.dto.LoginResponse;
import com.JavaBackEnd.spring_boot_journey_week6_day1.dto.RegisterRequest;
import com.JavaBackEnd.spring_boot_journey_week6_day1.entity.RefreshToken;
import com.JavaBackEnd.spring_boot_journey_week6_day1.entity.User;
import com.JavaBackEnd.spring_boot_journey_week6_day1.entity.UserRole;
import com.JavaBackEnd.spring_boot_journey_week6_day1.repository.UserRepository;
import com.JavaBackEnd.spring_boot_journey_week6_day1.security.CustomUserDetails;
import com.JavaBackEnd.spring_boot_journey_week6_day1.security.JwtService;
import com.JavaBackEnd.spring_boot_journey_week6_day1.service.AuthService;
import com.JavaBackEnd.spring_boot_journey_week6_day1.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtService jwtService,
                           JwtProperties jwtProperties,
                           RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User(
            request.getName(),
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            UserRole.USER
        );

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        CustomUserDetails userDetails =
            (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        String accessToken = jwtService.generateToken(userDetails);
        RefreshToken refreshToken =
            refreshTokenService.createRefreshToken(user, httpRequest);

        return new LoginResponse(
            accessToken,
            refreshToken.getToken(),
            user.getEmail(),
            user.getName(),
            user.getRole(),
            jwtProperties.getExpirationMs()
        );
    }

    @Override
    @Transactional
    public LoginResponse refreshAccessToken(String refreshTokenValue) {
        RefreshToken refreshToken =
            refreshTokenService.validateRefreshToken(refreshTokenValue);

        User user = refreshToken.getUser();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String newAccessToken = jwtService.generateToken(userDetails);

        return new LoginResponse(
            newAccessToken,
            refreshTokenValue,
            user.getEmail(),
            user.getName(),
            user.getRole(),
            jwtProperties.getExpirationMs()
        );
    }

    @Override
    @Transactional
    public void logout(String refreshTokenValue, User currentUser) {
        refreshTokenService.revokeRefreshToken(refreshTokenValue);
    }

    @Override
    @Transactional
    public void logoutAll(User currentUser) {
        refreshTokenService.revokeAllUserTokens(currentUser);
    }
}