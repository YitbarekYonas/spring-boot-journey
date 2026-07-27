package com.JavaBackEnd.spring_boot_journey_week5_day6.service.impl;

import com.JavaBackEnd.spring_boot_journey_week5_day6.config.JwtProperties;
import com.JavaBackEnd.spring_boot_journey_week5_day6.entity.RefreshToken;
import com.JavaBackEnd.spring_boot_journey_week5_day6.entity.User;
import com.JavaBackEnd.spring_boot_journey_week5_day6.repository.RefreshTokenRepository;
import com.JavaBackEnd.spring_boot_journey_week5_day6.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final int MAX_ACTIVE_TOKENS_PER_USER = 5;

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository,
                                   JwtProperties jwtProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user, HttpServletRequest request) {
        long activeTokens = refreshTokenRepository.countValidTokensByUser(
            user, LocalDateTime.now());

        if (activeTokens >= MAX_ACTIVE_TOKENS_PER_USER) {
            List<RefreshToken> validTokens =
                refreshTokenRepository.findValidTokensByUser(
                    user, LocalDateTime.now());
            if (!validTokens.isEmpty()) {
                validTokens.get(0).revoke();
                refreshTokenRepository.save(validTokens.get(0));
            }
        }

        String tokenValue = UUID.randomUUID().toString();

        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(
            jwtProperties.getRefreshExpirationMs() / 1000);

        String userAgent = request != null
            ? request.getHeader("User-Agent")
            : "unknown";

        RefreshToken refreshToken = new RefreshToken(
            tokenValue,
            user,
            expiresAt,
            userAgent
        );

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken validateRefreshToken(String tokenValue) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Refresh token not found"));

        if (refreshToken.isRevoked()) {
            refreshTokenRepository.revokeAllUserTokens(
                refreshToken.getUser());
            throw new IllegalStateException(
                "Refresh token has been revoked. "
                + "All sessions invalidated for security.");
        }

        if (refreshToken.isExpired()) {
            throw new IllegalStateException(
                "Refresh token has expired. Please log in again.");
        }

        return refreshToken;
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String tokenValue) {
        refreshTokenRepository.findByToken(tokenValue)
                .ifPresent(token -> {
                    token.revoke();
                    refreshTokenRepository.save(token);
                });
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.revokeAllUserTokens(user);
    }

    @Override
    @Transactional
    public int cleanupExpiredTokens() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        return refreshTokenRepository.deleteExpiredTokens(cutoff);
    }
}