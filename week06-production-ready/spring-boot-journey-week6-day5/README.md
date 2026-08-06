# Week 6, Day 5: Lombok Deep Dive

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-5-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Lombok-orange.svg)]()

> **"Lombok reduces boilerplate, but only if used correctly with JPA."**

---

## 🎯 Learning Objectives

- ✅ Use @Getter/@Setter on DTOs
- ✅ Use @Builder for clean object creation
- ✅ Use @Slf4j for logging
- ✅ Understand @Data risks on JPA entities
- ✅ Exclude relationships from @ToString
- ✅ Create safe entities with Lombok

---

## 💡 What I Learned Today

### 1. Lombok Annotations Summary

| Annotation | Purpose | Safe on JPA? |
|------------|---------|--------------|
| `@Getter` | Generate getters | ✅ Safe |
| `@Setter` | Generate setters | ⚠️ Careful with relationships |
| `@ToString` | Generate toString() | ⚠️ Exclude relationships |
| `@EqualsAndHashCode` | Generate equals/hashCode | ❌ Not safe (causes issues) |
| `@NoArgsConstructor` | No-arg constructor | ✅ Required for JPA |
| `@AllArgsConstructor` | All-arg constructor | ✅ Safe |
| `@Builder` | Builder pattern | ✅ Safe for DTOs |
| `@Data` | Getter+Setter+ToString+Equals | ❌ NEVER use on JPA entities |
| `@Slf4j` | Logger field | ✅ Safe |

### 2. @Data Dangers on Entities

```java
@Entity
@Data  // ❌ NEVER on JPA entities!
public class User {
    @Id
    private Long id;
    private String name;
    @OneToMany(mappedBy = "user")
    private List<Order> orders;
}

// Problems:
// 1. toString() → StackOverflowError (circular reference)
// 2. equals/hashCode → breaks HashSet with lazy loading
// 3. Setters on relationships → breaks bidirectional sync
```

### 3. Safe Entity with Lombok

```java
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"orders", "roles"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;

    @Builder
    public User(String name) {
        this.name = name;
    }
}
```

### 4. @Slf4j for Logging

```java
@Slf4j
@Service
public class UserService {
    public void createUser(String name) {
        log.info("Creating user: {}", name);
        log.warn("User creation attempted with empty name");
        log.error("Failed to create user", exception);
    }
}
```

### 5. @Builder on DTOs

```java
@Builder
@Getter
@AllArgsConstructor
public class CreateUserRequest {
    @NotBlank
    private String name;
    
    @Email
    @NotBlank
    private String email;
}

// Usage
CreateUserRequest request = CreateUserRequest.builder()
    .name("John Doe")
    .email("john@example.com")
    .build();
```

---

## 💻 Code Examples

### Safe DTO with Lombok

```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private BigDecimal price;
}
```

### Repository with @Slf4j

```java
@Slf4j
@Repository
public class BookRepositoryImpl {
    public Book save(Book book) {
        log.debug("Saving book: {}", book.getTitle());
        if (book.getPrice() == null) {
            log.warn("Book price is null for: {}", book.getTitle());
        }
        return bookRepository.save(book);
    }
}
```

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| `@Data` on JPA entities | Use individual annotations |
| `@ToString` without exclude | Add `exclude = {"relationships"}` |
| `@EqualsAndHashCode` on entities | Don't use - implement manually |
| No `@NoArgsConstructor` | JPA requires it |
| `@Builder` on entities | Safe, but use with caution |

---

## ✅ Day 5 Checklist

### Annotations
- [x] @Getter/@Setter on DTOs
- [x] @Builder on DTOs
- [x] @Slf4j for logging
- [x] @ToString(exclude) on entities
- [x] @NoArgsConstructor(access = PROTECTED)

---
