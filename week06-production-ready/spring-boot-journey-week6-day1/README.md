# Week 6, Day 1: Role-Based Authorization

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-1-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Role%20Based%20Authorization-orange.svg)]()

> **"Authorization decides what you can do after authentication proves who you are."**

---

## 🎯 Learning Objectives

- ✅ Understand Roles vs Authorities
- ✅ Use @EnableMethodSecurity
- ✅ Implement @PreAuthorize on service methods
- ✅ Apply @PreAuthorize with SpEL expressions
- ✅ Restrict DELETE endpoints to ADMIN only
- ✅ Test authorization with Postman matrix

---

## 💡 What I Learned Today

### 1. Roles vs Authorities

| Concept | Description | Example |
|---------|-------------|---------|
| **Role** | Named collection of permissions | `ADMIN`, `USER` |
| **Authority** | Individual permission string | `ROLE_ADMIN`, `DELETE_BOOK` |
| **ROLE_ prefix** | Required for `hasRole()` checks | `ROLE_ADMIN` in GrantedAuthority |

### 2. @PreAuthorize Syntax

```java
// Single role
@PreAuthorize("hasRole('ADMIN')")

// Multiple roles
@PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")

// Owner or admin pattern
@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")

// Authority check
@PreAuthorize("hasAuthority('DELETE_BOOK')")
```

### 3. Where to Put @PreAuthorize

| Layer | Pros | Cons |
|-------|------|------|
| Controller | Visible at HTTP boundary | Bypassable if service called directly |
| Service | Enforced regardless of caller | Hidden from API contract |
| Both | Defense in depth | Duplicate annotations |

### 4. Key SpEL Expressions

| Expression | Purpose |
|------------|---------|
| `hasRole('ADMIN')` | Check role (adds ROLE_ prefix) |
| `hasAnyRole('ADMIN', 'USER')` | Check multiple roles |
| `isAuthenticated()` | Check if user is logged in |
| `permitAll()` | Allow all (rare in service layer) |
| `#paramName` | Reference method parameter |
| `authentication.principal` | Access current user |
| `@beanName.method()` | Call Spring bean method |

### 5. @EnableMethodSecurity

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)  // Required for @PreAuthorize
public class SecurityConfig { }
```

---

## 💻 Code Examples

### URL-Level Security
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    .anyRequest().authenticated()
)
```

### Method-Level Security
```java
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
    bookService.deleteBook(id);
    return ResponseEntity.noContent().build();
}
```

### Owner or Admin Pattern
```java
@PreAuthorize("""
    hasAnyRole('ADMIN', 'LIBRARIAN')
    or #memberId == authentication.principal.userId
    """)
public List<LoanDto> getLoansByMember(Long memberId) { ... }
```

---

## 📋 Postman Authorization Matrix

### Environment Variables

| Variable | Value |
|----------|-------|
| `baseUrl` | `http://localhost:8080` |
| `apiVersion` | `/api` |
| `adminToken` | Set from admin login |
| `userToken` | Set from user login |

### Test Users

| Email | Password | Role |
|-------|----------|------|
| `admin@example.com` | `admin123` | ADMIN |
| `user@example.com` | `user123` | USER |

### Test Cases

| Test | Method | URL | Auth | Expected |
|------|--------|-----|------|----------|
| Create Book (Admin) | POST | `/books` | Admin | 201 |
| Create Book (User) | POST | `/books` | User | 403 |
| Delete Book (Admin) | DELETE | `/books/1` | Admin | 204 |
| Delete Book (User) | DELETE | `/books/1` | User | 403 |
| Get Admin Users (Admin) | GET | `/admin/users` | Admin | 200 |
| Get Admin Users (User) | GET | `/admin/users` | User | 403 |

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| Missing @EnableMethodSecurity | Add to config class |
| @PreAuthorize on private method | Make it public |
| Self-invocation bypasses proxy | Extract to separate class |
| Wrong ROLE_ prefix in @Secured | Use "ROLE_ADMIN" format |
| Rules in wrong order | Specific before general |

---

## ✅ Day 1 Checklist

### Concepts
- [x] Roles vs Authorities
- [x] @EnableMethodSecurity
- [x] @PreAuthorize with SpEL
- [x] Owner or admin pattern

### Code
- [x] SecurityConfig with role-based rules
- [x] AdminController with @PreAuthorize
- [x] BookController with @PreAuthorize
- [x] DataSeeder with ADMIN and USER roles

### Testing
- [x] Admin can delete books → 204
- [x] User cannot delete books → 403
- [x] Admin can access /admin/** → 200
- [x] User cannot access /admin/** → 403

---
