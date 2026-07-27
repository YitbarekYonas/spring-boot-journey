package com.JavaBackEnd.spring_boot_journey_week5_day6.service.impl;

import com.JavaBackEnd.spring_boot_journey_week5_day6.dto.LoginRequest;
import com.JavaBackEnd.spring_boot_journey_week5_day6.dto.LoginResponse;
import com.JavaBackEnd.spring_boot_journey_week5_day6.dto.RefreshTokenRequest;
import com.JavaBackEnd.spring_boot_journey_week5_day6.dto.RefreshTokenResponse;
import com.JavaBackEnd.spring_boot_journey_week5_day6.entity.RefreshToken;
import com.JavaBackEnd.spring_boot_journey_week5_day6.entity.User;
import com.JavaBackEnd.spring_boot_journey_week5_day6.security.CustomUserDetails;
import com.JavaBackEnd.spring_boot_journey_week5_day6.security.JwtService;
import com.JavaBackEnd.spring_boot_journey_week5_day6.config.JwtProperties;
import com.JavaBackEnd.spring_boot_journey_week5_day6.service.AuthService;
import com.JavaBackEnd.spring_boot_journey_week5_day6.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final DataSeeder jwtService;
    private final JwtProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           DataSeeder jwtService,
                           JwtProperties jwtProperties,
                           RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.refreshTokenService = refreshTokenService;
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
}