# Week 5, Day 1: Spring Security Fundamentals

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-1-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Security%20Fundamentals-orange.svg)]()

> **"Security sits in front of everything - every request must pass the filter chain."**

---

## 🎯 Learning Objectives

- ✅ Understand Spring Security filter chain
- ✅ Configure SecurityFilterChain with HttpSecurity
- ✅ Use HTTP Basic authentication
- ✅ Create in-memory users with UserDetailsService
- ✅ Use BCryptPasswordEncoder
- ✅ Distinguish between 401 and 403 responses

---

## 💡 What I Learned Today

### 1. The Filter Chain

```
HTTP Request
    → Spring Security Filter Chain (15+ filters)
    → DispatcherServlet
    → Controller
    → Response
```

Security intercepts requests BEFORE they reach your controllers.

### 2. SecurityFilterChain (Modern Approach)

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
            .anyRequest().authenticated()
        )
        .httpBasic(Customizer.withDefaults())
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .csrf(csrf -> csrf.disable());
    return http.build();
}
```

### 3. 401 vs 403

| Status | Meaning |
|--------|---------|
| **401 Unauthorized** | Not authenticated - "I don't know who you are" |
| **403 Forbidden** | Authenticated but not authorized - "I know who you are, you can't do this" |

### 4. In-Memory Users

```java
@Bean
public UserDetailsService userDetailsService(PasswordEncoder encoder) {
    UserDetails admin = User.builder()
        .username("admin")
        .password(encoder.encode("admin123"))
        .roles("ADMIN")
        .build();
    return new InMemoryUserDetailsManager(admin);
}
```

### 5. PasswordEncoder

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

---

## 💻 Code Examples

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
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
            User.builder()
                .username("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### SecurityContextHolder
```java
// Get current user anywhere
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();
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
| `admin` | `admin123` | ADMIN |
| `librarian` | `lib123` | LIBRARIAN |
| `member` | `mem123` | MEMBER |

### Endpoints

| Method | URL | Auth | Status |
|--------|-----|------|--------|
| GET | `/books` | None | 200 OK |
| GET | `/books/1` | None | 200 OK |
| POST | `/books` | None | 401 |
| POST | `/books` | Basic Auth | 201 Created |
| GET | `/debug/filters` | None | 200 OK |

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| Missing @EnableWebSecurity | Add annotation |
| Wrong rule order | Specific rules before anyRequest() |
| No PasswordEncoder bean | Define BCryptPasswordEncoder |
| Forgetting CSRF disable for REST | csrf().disable() |
| Using WebSecurityConfigurerAdapter | Use SecurityFilterChain bean |

---

## ✅ Day 1 Checklist

### Concepts
- [x] Filter chain concept
- [x] SecurityFilterChain configuration
- [x] HTTP Basic authentication
- [x] 401 vs 403 distinction
- [x] SecurityContextHolder

### Code
- [x] SecurityConfig with SecurityFilterChain
- [x] Public GET endpoints (permitAll)
- [x] Protected POST/PUT/DELETE
- [x] In-memory users (admin, librarian, member)
- [x] BCryptPasswordEncoder
- [x] Stateless session management
- [x] CSRF disabled

### Testing
- [x] Unauthenticated GET → 200
- [x] Unauthenticated POST → 401
- [x] Authenticated POST → 201
- [x] 401 vs 403 observed

---

```