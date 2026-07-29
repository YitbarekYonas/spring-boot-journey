# Week 5, Day 7: JWT Auth Service - Mini-Project

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-7-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Mini%20Project-orange.svg)]()

> **"Complete JWT authentication system from registration to refresh tokens."**

---

## 🎯 Learning Objectives

- ✅ Build standalone JWT Auth Service
- ✅ Implement complete authentication pipeline
- ✅ Use BCrypt password hashing
- ✅ Implement JWT access + refresh tokens
- ✅ Add role-based authorization (USER, ADMIN)
- ✅ Handle token expiry with refresh flow
- ✅ Implement logout and logout-all
- ✅ Create structured error responses
- ✅ Test full lifecycle with Postman

---

## 💡 What I Learned Today

### 1. Complete Auth Pipeline

```
Register → Login (access + refresh tokens) → 
Protected endpoints → Refresh token → Logout → Logout-all
```

### 2. Week 5 Security Chain

```
Day 1: Filter Chain & HTTP Basic
Day 2: UserDetails & Authentication/Authorization
Day 3: BCrypt & Registration/Login
Day 4: JWT Generation & Validation
Day 5: JWT Filter & SecurityConfig Integration
Day 6: Refresh Tokens & Token Expiry
Day 7: Complete Auth Service Mini-Project
```

### 3. Key Components

| Component | Purpose |
|-----------|---------|
| User | Entity with email, password, role |
| RefreshToken | Long-lived revocable token in DB |
| JwtService | Generate/validate JWT tokens |
| JwtAuthenticationFilter | Intercept requests, validate JWT |
| SecurityConfig | Configure security rules |
| AuthService | Registration, login, refresh, logout |

### 4. Token Lifecycle

```
1. Login → access token (15 min) + refresh token (7 days)
2. Access token expired → 401 with "action": "REFRESH_TOKEN"
3. Refresh → new access token
4. Logout → revoke refresh token
5. Logout-all → revoke ALL refresh tokens
```

---

## 💻 Code Examples

### Register

```java
@PostMapping("/register")
public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(authService.register(request));
}
```

### Login

```java
@PostMapping("/login")
public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request,
                                           HttpServletRequest httpRequest) {
    return ResponseEntity.ok(authService.login(request, httpRequest));
}
```

### Refresh

```java
@PostMapping("/refresh")
public ResponseEntity<RefreshTokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
    return ResponseEntity.ok(authService.refreshAccessToken(request));
}
```

### Logout

```java
@PostMapping("/logout")
public ResponseEntity<Map<String, String>> logout(
        @RequestBody RefreshTokenRequest request,
        @AuthenticationPrincipal CustomUserDetails currentUser) {
    authService.logout(request.getRefreshToken(), currentUser.getUser());
    return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
}
```

---

## 📋 Postman Endpoints

### Environment Variables

| Variable | Value |
|----------|-------|
| `baseUrl` | `http://localhost:8080` |
| `apiVersion` | `/api` |
| `accessToken` | Set from login |
| `refreshToken` | Set from login |

### Public Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| POST | `/auth/register` | Register new user |
| POST | `/auth/login` | Login - get tokens |
| POST | `/auth/refresh` | Refresh access token |

### Protected Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/auth/me` | Get current user |
| POST | `/auth/change-password` | Change password |
| POST | `/auth/logout` | Logout current device |
| POST | `/auth/logout-all` | Logout all devices |

### Admin Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/admin/users` | Get all users |
| PATCH | `/admin/users/{id}/disable` | Disable user |

---

## 📊 Error Responses

### 401 - Unauthorized (Expired Token)
```json
{
    "timestamp": "2026-07-29T22:55:00",
    "status": 401,
    "error": "Unauthorized",
    "message": "Access token has expired. Use your refresh token at /api/auth/refresh to get a new one.",
    "action": "REFRESH_TOKEN",
    "path": "/api/auth/me"
}
```

### 403 - Forbidden (Wrong Role)
```json
{
    "timestamp": "2026-07-29T22:55:00",
    "status": 403,
    "error": "Forbidden",
    "message": "You don't have permission to access this resource.",
    "path": "/api/admin/users"
}
```

---

## ✅ Mini-Project Checklist

### Concepts
- [x] Complete JWT authentication pipeline
- [x] BCrypt password hashing
- [x] Access + refresh token system
- [x] Role-based authorization
- [x] Token revocation and expiry

### Code
- [x] User, RefreshToken entities
- [x] JwtService (generate, validate)
- [x] JwtAuthenticationFilter
- [x] SecurityConfig with JWT
- [x] AuthService (register, login, refresh, logout)
- [x] AuthException with errorCode
- [x] GlobalAuthExceptionHandler
- [x] AdminController with @PreAuthorize
- [x] DataSeeder with test users

### Testing
- [x] Register → 201
- [x] Login → access + refresh tokens
- [x] Protected endpoint with token → 200
- [x] Expired token → 401 with REFRESH_TOKEN action
- [x] Refresh token → new access token
- [x] Logout → token revoked
- [x] Admin endpoint with ADMIN role → 200
- [x] Admin endpoint with USER role → 403
- [x] No token → 401

---
