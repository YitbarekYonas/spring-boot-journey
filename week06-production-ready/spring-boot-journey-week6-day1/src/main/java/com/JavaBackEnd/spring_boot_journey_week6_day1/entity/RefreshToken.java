package com.JavaBackEnd.spring_boot_journey_week6_day1.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean revoked = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    protected RefreshToken() {}

    public RefreshToken(String token, User user,
                        LocalDateTime expiresAt, String userAgent) {
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
        this.userAgent = userAgent;
        this.createdAt = LocalDateTime.now();
        this.revoked = false;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }

    public void revoke() {
        this.revoked = true;
    }

    public Long getId() { return id; }
    public String getToken() { return token; }
    public User getUser() { return user; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public boolean isRevoked() { return revoked; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getUserAgent() { return userAgent; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }
}