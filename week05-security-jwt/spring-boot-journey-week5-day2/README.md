# Week 5, Day 2: Authentication vs Authorization

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-2-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Authentication%20vs%20Authorization-orange.svg)]()

> **"Authentication proves who you are. Authorization proves what you can do."**

---

## 🎯 Learning Objectives

- ✅ Distinguish between Authentication and Authorization
- ✅ Create User entity with BCrypt password hashing
- ✅ Implement CustomUserDetails wrapping User entity
- ✅ Implement CustomUserDetailsService loading from database
- ✅ Configure DaoAuthenticationProvider
- ✅ Apply URL-level role-based authorization
- ✅ Test 401 (no auth) vs 403 (wrong role)

---

## 💡 What I Learned Today

### 1. Authentication vs Authorization

| Concept | Meaning | Handled By |
|---------|---------|------------|
| **Authentication** | "Who are you?" | UserDetailsService + PasswordEncoder |
| **Authorization** | "What can you do?" | Roles in SecurityFilterChain |

### 2. UserDetails Contract

```java
public interface UserDetails {
    String getUsername();      // email used as username
    String getPassword();      // BCrypt hash
    Collection<? extends GrantedAuthority> getAuthorities(); // ROLE_ADMIN
    boolean isEnabled();
    boolean isAccountNonExpired();
    boolean isAccountNonLocked();
    boolean isCredentialsNonExpired();
}
```

### 3. CustomUserDetails
Wraps your User entity and adapts it to Spring Security's contract.

### 4. UserDetailsService
```java
@Override
public UserDetails loadUserByUsername(String email) {
    return userRepository.findByEmail(email)
        .map(CustomUserDetails::new)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
}
```

### 5. BCrypt Password Hashing
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// Seeding users
userRepository.save(new User(
    "Admin User",
    "admin@library.com",
    passwordEncoder.encode("admin123"),  // BCrypt hash stored
    UserRole.ADMIN
));
```

### 6. Role-Based URL Authorization

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
    .requestMatchers(HttpMethod.POST, "/api/books").hasRole("ADMIN")
    .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")
    .requestMatchers("/api/loans/checkout").hasAnyRole("ADMIN", "LIBRARIAN")
    .anyRequest().authenticated()
)
```

### 7. Status Codes

| Status | Meaning |
|--------|---------|
| **401 Unauthorized** | Not authenticated - "I don't know who you are" |
| **403 Forbidden** | Authenticated but not authorized - "I know who you are, you can't do this" |

### 8. @AuthenticationPrincipal
```java
@GetMapping("/me")
public String getMe(@AuthenticationPrincipal CustomUserDetails user) {
    return "Hello " + user.getUser().getName();
}
```

---

## 💻 Code Examples

### User Entity
```java
@Entity
public class User extends AuditableEntity {
    @Id @GeneratedValue private Long id;
    @Column(unique = true) private String email;
    private String password;  // BCrypt hash
    @Enumerated(EnumType.STRING) private UserRole role;
    private boolean enabled = true;
    private boolean accountNonLocked = true;
    // ... getters/setters
}
```

### CustomUserDetails
```java
public class CustomUserDetails implements UserDetails {
    private final User user;
    
    @Override
    public String getUsername() { return user.getEmail(); }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }
    // ... delegate other methods to user
}
```

### SecurityConfig
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/books").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .csrf(csrf -> csrf.disable());
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## 📋 Postman Testing

### Environment Variables

| Variable | Value |
|----------|-------|
| `baseUrl` | `http://localhost:8080` |
| `apiVersion` | `/api` |

### Test Users

| Username | Password | Role |
|----------|----------|------|
| `admin@library.com` | `admin123` | ADMIN |
| `jane@library.com` | `lib123` | LIBRARIAN |
| `john@library.com` | `mem123` | MEMBER |

### Endpoints

| Method | URL | Auth | Expected |
|--------|-----|------|----------|
| GET | `/books` | None | 200 OK |
| GET | `/books/1` | None | 200 OK |
| POST | `/books` | None | 401 |
| POST | `/books` | Member | 403 |
| POST | `/books` | Admin | 201 |
| DELETE | `/books/4` | None | 401 |
| DELETE | `/books/4` | Librarian | 403 |
| DELETE | `/books/4` | Admin | 204 |
| GET | `/auth/me` | Any | 200 |

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| Forgetting ROLE_ prefix | Use `"ROLE_" + role.name()` |
| Missing BCrypt encoding | Use `passwordEncoder.encode()` when seeding |
| Using plaintext passwords | Always use BCrypt |
| Wrong pattern for return | Use `/api/loans/{id}/return` not `/**/return` |
| Missing `@Transactional` on loadUserByUsername | Add `@Transactional(readOnly = true)` |

---

## ✅ Day 2 Checklist

### Authentication
- [x] User entity with BCrypt password
- [x] CustomUserDetails wrapper
- [x] CustomUserDetailsService with UserRepository
- [x] PasswordEncoder bean (BCrypt)
- [x] Seeded users with hashed passwords

### Authorization
- [x] Role-based URL rules in SecurityFilterChain
- [x] ADMIN role restrictions
- [x] LIBRARIAN role restrictions
- [x] MEMBER role restrictions

### Testing
- [x] Public GET → 200
- [x] No auth POST → 401
- [x] Wrong role POST → 403
- [x] Correct role POST → 201
- [x] BCrypt hashes in database

---
