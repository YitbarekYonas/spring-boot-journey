package com.JavaBackEnd.jwtauthservice.scheduler;

import com.JavaBackEnd.jwtauthservice.service.RefreshTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TokenCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(TokenCleanupScheduler.class);

    private final RefreshTokenService refreshTokenService;

    public TokenCleanupScheduler(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredTokens() {
        int deleted = refreshTokenService.cleanupExpiredTokens();
        log.info("Cleaned up {} expired refresh tokens", deleted);
    }
}