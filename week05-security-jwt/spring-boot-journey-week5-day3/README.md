# Week 5, Day 3: BCrypt & Password Handling

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-3-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Password%20Handling-orange.svg)]()

> **"Never store plaintext passwords. BCrypt hashes are salted, adaptive, and one-way."**

---

## 🎯 Learning Objectives

- ✅ Understand why plaintext passwords are catastrophic
- ✅ Use BCryptPasswordEncoder for hashing
- ✅ Create registration endpoint
- ✅ Validate password strength
- ✅ Handle duplicate email errors
- ✅ Test login with AuthenticationManager

---

## 💡 What I Learned Today

### 1. Why BCrypt?

| Feature | BCrypt |
|---------|--------|
| **One-way hash** | Cannot reverse to original password |
| **Salted** | Same password → different hash each time |
| **Adaptive** | Work factor can be increased over time |
| **Safe compare** | Timing-attack resistant |

### 2. BCrypt Password Flow

```
Registration:
User sends password → BCryptPasswordEncoder.encode() → Store hash in DB

Login:
User sends password → BCryptPasswordEncoder.matches(raw, storedHash) → true/false
```

### 3. Registration Endpoint

```java
@PostMapping("/register")
public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    if (userRepository.existsByEmail(request.email())) {
        return ResponseEntity.badRequest().body("Email already exists");
    }

    User user = new User(
        request.name(),
        request.email(),
        passwordEncoder.encode(request.password()),
        UserRole.MEMBER
    );
    userRepository.save(user);
    return ResponseEntity.ok("User registered successfully");
}
```

### 4. Login Endpoint

```java
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    try {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
            )
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        return ResponseEntity.ok("Login successful");
    } catch (BadCredentialsException e) {
        return ResponseEntity.status(401).body("Invalid credentials");
    }
}
```

### 5. AuthenticationManager

```java
@Bean
public AuthenticationManager authenticationManager(
        AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
}
```

### 6. Password Validation

```java
private boolean isValidPassword(String password) {
    // At least 8 characters, one uppercase, one lowercase, one digit
    return password.length() >= 8 &&
           password.matches(".*[A-Z].*") &&
           password.matches(".*[a-z].*") &&
           password.matches(".*\\d.*");
}
```

---

## 💻 Code Examples

### RegisterRequest DTO
```java
public record RegisterRequest(
    String name,
    String email,
    String password
) {}
```

### LoginRequest DTO
```java
public record LoginRequest(
    String email,
    String password
) {}
```

### AuthController
```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        if (!isValidPassword(request.password())) {
            return ResponseEntity.badRequest()
                .body("Password must be 8+ chars with uppercase, lowercase, and digit");
        }

        User user = new User(
            request.name(),
            request.email(),
            passwordEncoder.encode(request.password()),
            UserRole.MEMBER
        );
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.email(),
                    request.password()
                )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "email", request.email()
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid email or password"));
        }
    }
}
```

### SecurityConfig Updates
```java
@Bean
public AuthenticationManager authenticationManager(
        AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
}
```

---

## 📋 Postman Testing

### Test Users

| Username | Password | Role |
|----------|----------|------|
| `admin@library.com` | `admin123` | ADMIN |
| `jane@library.com` | `lib123` | LIBRARIAN |
| `john@library.com` | `mem123` | MEMBER |

### Endpoints

| Method | URL | Body | Expected |
|--------|-----|------|----------|
| POST | `/api/auth/register` | {name, email, password} | 201 Created |
| POST | `/api/auth/register` | {name, email, password} | 400 Bad Request (duplicate) |
| POST | `/api/auth/login` | {email, password} | 200 OK |
| POST | `/api/auth/login` | {email, wrong password} | 401 Unauthorized |

### Register Request
```json
{
    "name": "New User",
    "email": "new@library.com",
    "password": "Password123"
}
```

### Login Request
```json
{
    "email": "new@library.com",
    "password": "Password123"
}
```

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| Storing plaintext passwords | Always use BCrypt |
| Forgetting `@Transactional` on register | Add annotation |
| Not checking duplicate email | Check existsByEmail before saving |
| Catching wrong exception | Catch BadCredentialsException |
| Not validating password strength | Add validation before encoding |

---

## ✅ Day 3 Checklist

### Concepts
- [x] BCrypt hashing mechanism
- [x] Registration flow
- [x] Login flow with AuthenticationManager
- [x] Password validation
- [x] Duplicate email handling

### Code
- [x] RegisterRequest DTO
- [x] LoginRequest DTO
- [x] Registration endpoint
- [x] Login endpoint with AuthenticationManager
- [x] Password validation logic
- [x] Duplicate email check

### Testing
- [x] Register new user → 201
- [x] Register duplicate email → 400
- [x] Login with valid credentials → 200
- [x] Login with invalid credentials → 401
- [x] Password validation works

---
