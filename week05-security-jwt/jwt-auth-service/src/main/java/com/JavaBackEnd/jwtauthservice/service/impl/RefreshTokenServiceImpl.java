package com.JavaBackEnd.jwtauthservice.service.impl;

import com.JavaBackEnd.jwtauthservice.config.JwtProperties;
import com.JavaBackEnd.jwtauthservice.entity.RefreshToken;
import com.JavaBackEnd.jwtauthservice.entity.User;
import com.JavaBackEnd.jwtauthservice.exception.AuthException;
import com.JavaBackEnd.jwtauthservice.repository.RefreshTokenRepository;
import com.JavaBackEnd.jwtauthservice.service.RefreshTokenService;
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
            tokenValue, user, expiresAt, userAgent
        );

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken validateRefreshToken(String tokenValue) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(tokenValue)
                .orElseThrow(AuthException::tokenNotFound);

        if (refreshToken.isRevoked()) {
            refreshTokenRepository.revokeAllUserTokens(refreshToken.getUser());
            throw AuthException.tokenRevoked();
        }

        if (refreshToken.isExpired()) {
            throw AuthException.tokenExpired();
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