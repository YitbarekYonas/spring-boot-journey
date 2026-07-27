# Week 5, Day 6: Refresh Tokens & Token Expiry Handling

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-6-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Refresh%20Tokens-orange.svg)]()

> **"Refresh tokens extend sessions securely without re-entering credentials."**

---

## 🎯 Learning Objectives

- ✅ Understand two-token authentication system
- ✅ Create RefreshToken entity with revocability
- ✅ Implement RefreshTokenService
- ✅ Add /auth/refresh endpoint
- ✅ Add /auth/logout and /auth/logout-all endpoints
- ✅ Handle expired tokens with "action": "REFRESH_TOKEN"
- ✅ Test complete refresh token flow

---

## 💡 What I Learned Today

### 1. Two-Token System

| Token | Lifetime | Purpose | Storage |
|-------|----------|---------|---------|
| **Access Token** | Short (2 hours) | API requests | Client memory |
| **Refresh Token** | Long (7 days) | Get new access token | Database (revocable) |

### 2. Refresh Token Flow

```
1. Login → returns access token + refresh token
2. Access token expires → 401 with "action": "REFRESH_TOKEN"
3. Client calls /auth/refresh with refresh token
4. Server validates refresh token → returns new access token
5. Client retries original request with new token
```

### 3. Why Refresh Tokens Are Database-Backed

```
JWT refresh token → stateless, cannot be revoked
Random string refresh token → stored in DB, can be revoked instantly
Refresh tokens MUST be revocable → store in database
```

### 4. Key Endpoints

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/auth/login` | POST | Returns access + refresh tokens |
| `/auth/refresh` | POST | Get new access token |
| `/auth/logout` | POST | Revoke refresh token |
| `/auth/logout-all` | POST | Revoke ALL refresh tokens |

---

## 💻 Code Examples

### RefreshToken Entity

```java
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
    
    public boolean isValid() {
        return !revoked && !isExpired();
    }
    
    public void revoke() {
        this.revoked = true;
    }
}
```

### RefreshTokenService

```java
@Service
public class RefreshTokenService {
    public RefreshToken createRefreshToken(User user, HttpServletRequest request) {
        String tokenValue = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);
        RefreshToken token = new RefreshToken(tokenValue, user, expiresAt, userAgent);
        return refreshTokenRepository.save(token);
    }
    
    public RefreshToken validateRefreshToken(String tokenValue) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenValue)
            .orElseThrow(() -> new IllegalArgumentException("Token not found"));
        
        if (token.isRevoked()) {
            refreshTokenRepository.revokeAllUserTokens(token.getUser());
            throw new IllegalStateException("Refresh token revoked");
        }
        
        if (token.isExpired()) {
            throw new IllegalStateException("Refresh token expired");
        }
        
        return token;
    }
}
```

### Refresh Endpoint

```java
@PostMapping("/refresh")
public ResponseEntity<RefreshTokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
    RefreshToken refreshToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());
    User user = refreshToken.getUser();
    CustomUserDetails userDetails = new CustomUserDetails(user);
    String newAccessToken = jwtService.generateToken(userDetails);
    return ResponseEntity.ok(new RefreshTokenResponse(newAccessToken, jwtProperties.getExpirationMs()));
}
```

### Expired Token Handling

```java
// In JwtAuthenticationFilter
try {
    email = jwtService.extractEmail(token);
} catch (ExpiredJwtException e) {
    request.setAttribute("JWT_ERROR", "Token expired");
    filterChain.doFilter(request, response);
    return;
}

// In JwtAuthenticationEntryPoint
if ("Token expired".equals(jwtError)) {
    body.put("message", "Access token expired. Use /api/auth/refresh");
    body.put("action", "REFRESH_TOKEN");
}
```

---

## 📋 Postman Testing

### Environment Variables

| Variable | Value |
|----------|-------|
| `baseUrl` | `http://localhost:8080` |
| `apiVersion` | `/api` |
| `accessToken` | Set from login |
| `refreshToken` | Set from login |

### Test Sequence

#### 1. Login
```
POST /api/auth/login
Body: { "email": "admin@library.com", "password": "admin123" }
→ Saves accessToken + refreshToken
```

#### 2. Refresh Token
```
POST /api/auth/refresh
Body: { "refreshToken": "{{refreshToken}}" }
→ Returns new accessToken
```

#### 3. Logout
```
POST /api/auth/logout
Authorization: Bearer {{accessToken}}
Body: { "refreshToken": "{{refreshToken}}" }
→ Revokes refresh token
```

#### 4. Logout All
```
POST /api/auth/logout-all
Authorization: Bearer {{accessToken}}
→ Revokes ALL refresh tokens
```

---

## 📊 401 Response with Action

```json
{
    "timestamp": "2026-07-27T12:00:00",
    "status": 401,
    "error": "Unauthorized",
    "message": "Access token has expired. Use your refresh token at /api/auth/refresh to get a new one.",
    "action": "REFRESH_TOKEN",
    "path": "/api/auth/me"
}
```

---

## ✅ Day 6 Checklist

### Concepts
- [x] Two-token system (access + refresh)
- [x] Refresh tokens stored in database
- [x] Revocability of refresh tokens
- [x] logout-all revokes all sessions

### Code
- [x] RefreshToken entity
- [x] RefreshTokenRepository
- [x] RefreshTokenService
- [x] /auth/refresh endpoint
- [x] /auth/logout endpoint
- [x] /auth/logout-all endpoint
- [x] Expired token handling in filter
- [x] "action": "REFRESH_TOKEN" in 401 response

---
