# Week 5, Day 4: JWT Fundamentals

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-4-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-JWT-orange.svg)]()

> **"JWT enables stateless authentication - no database lookup per request."**

---

## 🎯 Learning Objectives

- ✅ Understand JWT structure (Header.Payload.Signature)
- ✅ Implement JwtService with generateToken and extractEmail
- ✅ Use JJWT library for token operations
- ✅ Test JWT generation and validation
- ✅ Understand stateless authentication
- ✅ Verify tokens on jwt.io

---

## 💡 What I Learned Today

### 1. JWT Structure

```
Header.Payload.Signature
```

| Part | Content | Purpose |
|------|---------|---------|
| Header | `{"alg":"HS256","typ":"JWT"}` | Algorithm and type |
| Payload | `{"sub":"email","role":"ADMIN","iat":...}` | Claims (user data) |
| Signature | `HMAC(header+payload, secret)` | Tamper-proof verification |

### 2. Key Concepts

| Concept | Meaning |
|---------|---------|
| **Stateless** | Server doesn't store session - verifies token cryptographically |
| **Claims** | Key-value pairs in payload (sub, iat, exp, custom) |
| **Secret Key** | Used to sign and verify tokens - MUST be 256+ bits |
| **Stateless Auth** | Client sends token in `Authorization: Bearer <token>` header |

### 3. Token Lifecycle

```
1. User logs in → Server verifies credentials
2. Server generates JWT → returns to client
3. Client sends JWT in every subsequent request
4. Server verifies signature and expiry → NO DB lookup!
```

### 4. Secret Key Rules
- ✅ Cryptographically random (32+ bytes for HS256)
- ✅ Never in source code or commit to git
- ✅ Loaded from environment variable
- ❌ Never human-chosen or short

---

## 💻 Code Examples

### JwtService - Core Methods

```java
@Service
public class JwtService {
    private final SecretKey signingKey;

    public String generateToken(CustomUserDetails userDetails) {
        return Jwts.builder()
            .subject(userDetails.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .claim("role", userDetails.getUser().getRole().name())
            .claim("userId", userDetails.getUserId())
            .signWith(signingKey)
            .compact();
    }

    public boolean isTokenValid(String token, CustomUserDetails userDetails) {
        final String email = extractEmail(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
```

### JwtProperties

```java
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private long expirationMs;
    private long refreshExpirationMs;
    // getters and setters
}
```

### Application Configuration

```yaml
jwt:
  secret: ${JWT_SECRET}
  expiration-ms: 7200000
  refresh-expiration-ms: 604800000
```

---

## 📊 JWT Claims

| Claim | Purpose |
|-------|---------|
| `sub` | Subject (email/username) |
| `iat` | Issued at timestamp |
| `exp` | Expiration timestamp |
| `role` | User role (custom claim) |
| `userId` | User ID (custom claim) |
| `name` | User name (custom claim) |

---

## 📋 Debug Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/debug/jwt/generate/{email}` | GET | Generate JWT for user |

---

## ✅ Day 4 Checklist

### Concepts
- [x] JWT structure (Header.Payload.Signature)
- [x] Stateless authentication
- [x] Claims (sub, iat, exp, custom)
- [x] Secret key requirements
- [x] JJWT library usage

### Code
- [x] JwtProperties
- [x] JwtService
  - [x] generateToken
  - [x] extractEmail
  - [x] extractRole
  - [x] extractUserId
  - [x] isTokenValid
  - [x] isTokenExpired

### Testing
- [x] generateToken returns valid token
- [x] extractEmail returns correct email
- [x] extractRole returns correct role
- [x] isTokenValid true for valid token
- [x] isTokenValid false for tampered token
- [x] isTokenExpired false for fresh token
- [x] All tests passing

---
