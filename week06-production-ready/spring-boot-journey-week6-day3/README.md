# Week 6, Day 3: Bean Validation

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-3-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Bean%20Validation-orange.svg)]()

> **"Validate at the boundary - reject bad input before it reaches business logic."**

---

## 🎯 Learning Objectives

- ✅ Add spring-boot-starter-validation dependency
- ✅ Use @Valid on controller methods
- ✅ Apply validation annotations to DTOs
- ✅ Create custom @PasswordMatch validator
- ✅ Remove redundant service validation
- ✅ Test all validation failure scenarios

---

## 💡 What I Learned Today

### 1. Why Validation Matters

Without validation, bad data reaches service layer and causes:
- Database constraint violations
- Unnecessary password hashing
- Generic error responses
- Wasted processing time

### 2. Core Validation Annotations

| Annotation | Purpose |
|------------|---------|
| `@NotBlank` | Not null + not empty + not whitespace |
| `@NotEmpty` | Not null + not empty |
| `@Size(min, max)` | String length constraints |
| `@Email` | Valid email format |
| `@Min` / `@Max` | Numeric range |
| `@Positive` | > 0 |
| `@Pattern` | Regex match |
| `@Past` / `@Future` | Date constraints |

### 3. The Validation Flow

```
Client → @Valid on @RequestBody → Bean Validation → 
Violations → MethodArgumentNotValidException → 
GlobalExceptionHandler → 400 with fieldErrors
```

### 4. Custom Cross-Field Validation

```java
@PasswordMatch(
    password = "newPassword",
    confirmPassword = "confirmNewPassword",
    message = "Passwords do not match"
)
public class ChangePasswordRequest {
    @NotBlank private String newPassword;
    @NotBlank private String confirmNewPassword;
}
```

---

## 💻 Code Examples

### DTO with Validation
```java
public class RegisterRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72)
    private String password;
}
```

### Controller with @Valid
```java
@PostMapping("/register")
public ResponseEntity<User> register(
        @Valid @RequestBody RegisterRequest request) {
    return ResponseEntity.ok(authService.register(request));
}
```

### Custom Validator Implementation
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchValidator.class)
public @interface PasswordMatch {
    String password();
    String confirmPassword();
    String message() default "Passwords do not match";
}
```

### Validator Logic
```java
public class PasswordMatchValidator 
        implements ConstraintValidator<PasswordMatch, Object> {
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext ctx) {
        Object password = new BeanWrapperImpl(value)
            .getPropertyValue("newPassword");
        Object confirm = new BeanWrapperImpl(value)
            .getPropertyValue("confirmNewPassword");
        return password != null && password.equals(confirm);
    }
}
```

---

## 📋 Postman Tests

### Environment Variables

| Variable | Value |
|----------|-------|
| `baseUrl` | `http://localhost:8080` |
| `apiVersion` | `/api` |

### Test Cases

| Test | Body | Status | Field Errors |
|------|------|--------|--------------|
| Valid | Valid data | 201 | None |
| Blank fields | `""`, `""`, `""` | 400 | name, email, password |
| Invalid email | `not-an-email` | 400 | email |
| Short password | `"ab"` | 400 | password |
| Password mismatch | Different values | 400 | confirmNewPassword |
| All violations | `""`, `"bad"`, `"ab"` | 400 | 3 errors |

### Error Response Schema
```json
{
    "timestamp": "2026-08-03T22:30:00",
    "status": 400,
    "error": "Bad Request",
    "errorCode": "VALIDATION_FAILED",
    "message": "Request validation failed. Check 'fieldErrors' for details.",
    "path": "/api/auth/register",
    "fieldErrors": [
        {
            "field": "name",
            "rejectedValue": "",
            "message": "Name is required"
        }
    ]
}
```

### Test Script
```javascript
pm.test("Multiple violations reported together", function() {
    pm.response.to.have.status(400);
    const body = pm.response.json();
    pm.expect(body.fieldErrors.length).to.be.above(0);
    body.fieldErrors.forEach(fe => {
        pm.expect(fe).to.have.property("field");
        pm.expect(fe).to.have.property("rejectedValue");
        pm.expect(fe).to.have.property("message");
    });
});
```

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| Missing @Valid | Add to controller parameter |
| Wrong import | Use jakarta.validation not javax |
| Missing starter | Add spring-boot-starter-validation |
| Service validation duplication | Remove redundant checks |
| @NotBlank vs @NotNull | Use @NotBlank for strings |
| Missing message | Always provide user-friendly messages |

---

## ✅ Day 3 Checklist

### Setup
- [x] spring-boot-starter-validation added
- [x] @EnableJpaAuditing enabled
- [x] PasswordEncoder bean configured

### DTOs
- [x] RegisterRequest with validation
- [x] LoginRequest with validation
- [x] ChangePasswordRequest with @PasswordMatch

### Validators
- [x] @PasswordMatch annotation
- [x] PasswordMatchValidator implementation

### Controller
- [x] @Valid on @RequestBody parameters

### Testing
- [x] Valid request → 201
- [x] Blank fields → 400 with fieldErrors
- [x] Invalid email → 400
- [x] Short password → 400
- [x] Password mismatch → 400
- [x] Multiple violations → all reported

---
