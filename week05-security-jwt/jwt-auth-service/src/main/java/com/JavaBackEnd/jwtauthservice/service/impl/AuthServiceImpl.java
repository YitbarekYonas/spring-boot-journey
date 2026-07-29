package com.JavaBackEnd.jwtauthservice.service.impl;

import com.JavaBackEnd.jwtauthservice.config.JwtProperties;
import com.JavaBackEnd.jwtauthservice.dto.*;
import com.JavaBackEnd.jwtauthservice.entity.RefreshToken;
import com.JavaBackEnd.jwtauthservice.entity.User;
import com.JavaBackEnd.jwtauthservice.entity.UserRole;
import com.JavaBackEnd.jwtauthservice.exception.AuthException;
import com.JavaBackEnd.jwtauthservice.repository.UserRepository;
import com.JavaBackEnd.jwtauthservice.security.CustomUserDetails;
import com.JavaBackEnd.jwtauthservice.security.JwtService;
import com.JavaBackEnd.jwtauthservice.service.AuthService;
import com.JavaBackEnd.jwtauthservice.service.RefreshTokenService;
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
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw AuthException.emailAlreadyExists(request.getEmail());
        }

        validatePassword(request.getPassword());

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
            request.getName(),
            request.getEmail(),
            hashedPassword,
            UserRole.USER
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
    public RefreshTokenResponse refreshAccessToken(RefreshTokenRequest request) {
        RefreshToken refreshToken =
            refreshTokenService.validateRefreshToken(
                request.getRefreshToken());

        User user = refreshToken.getUser();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String newAccessToken = jwtService.generateToken(userDetails);

        return new RefreshTokenResponse(
            newAccessToken,
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

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(
                    "User not found",
                    org.springframework.http.HttpStatus.NOT_FOUND,
                    "USER_NOT_FOUND"
                ));

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {
            throw AuthException.incorrectCurrentPassword();
        }

        validatePassword(request.getNewPassword());

        if (!request.getNewPassword()
                    .equals(request.getConfirmNewPassword())) {
            throw new AuthException(
                "New password and confirmation do not match",
                org.springframework.http.HttpStatus.BAD_REQUEST,
                "PASSWORD_MISMATCH"
            );
        }

        if (passwordEncoder.matches(
                request.getNewPassword(), user.getPassword())) {
            throw new AuthException(
                "New password must be different from current password",
                org.springframework.http.HttpStatus.BAD_REQUEST,
                "SAME_PASSWORD"
            );
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        refreshTokenService.revokeAllUserTokens(user);
    }

    private void validatePassword(String password) {
        if (password == null
                || password.length() < 8
                || password.length() > 72) {
            throw AuthException.weakPassword();
        }
    }
}