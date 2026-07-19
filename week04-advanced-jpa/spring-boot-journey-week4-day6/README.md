# Week 4, Day 6: Auditing & Query Optimization

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-6-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Auditing%20%26%20Optimization-orange.svg)]()

> **"Auditing tracks who did what and when. JOIN FETCH prevents N+1 performance disasters."**

---

## 🎯 Learning Objectives

- ✅ Enable JPA Auditing with `@EnableJpaAuditing`
- ✅ Use `@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`, `@LastModifiedBy`
- ✅ Create `AuditableEntity` base class with `@MappedSuperclass`
- ✅ Provide current user with `AuditorAware`
- ✅ Enable Hibernate statistics to detect N+1
- ✅ Fix N+1 with `JOIN FETCH`

---

## 💡 What I Learned Today

### 1. JPA Auditing Annotations

| Annotation | Purpose |
|------------|---------|
| `@CreatedDate` | Set when entity is first persisted |
| `@LastModifiedDate` | Set every time entity is saved |
| `@CreatedBy` | Set to current user when created |
| `@LastModifiedBy` | Set to current user when modified |

### 2. Enable Auditing

```java
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class Application { }
```

### 3. Auditable Base Class

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;
}
```

### 4. AuditorAware

```java
@Bean
public AuditorAware<String> auditorProvider() {
    return () -> Optional.of("system");
}
```

### 5. Hibernate Statistics

```yaml
spring:
  jpa:
    properties:
      hibernate:
        generate_statistics: true
logging:
  level:
    org.hibernate.stat: DEBUG
```

### 6. Fix N+1 with JOIN FETCH

```java
// ❌ N+1 - Lazy loading
List<Book> findAll();

// ✅ JOIN FETCH - One query
@Query("SELECT b FROM Book b JOIN FETCH b.author a")
List<Book> findAllWithAuthor();
```

---

## 💻 Code Examples

### AuditableEntity
```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;
}
```

### Repository with JOIN FETCH
```java
@Query("""
    SELECT b FROM Book b
    JOIN FETCH b.author a
    ORDER BY b.title ASC
    """)
List<Book> findAllWithAuthor();

@Query("""
    SELECT b FROM Book b
    JOIN FETCH b.author a
    WHERE b.id = :id
    """)
Optional<Book> findByIdWithAuthor(Long id);
```

### Controller
```java
@GetMapping
public ResponseEntity<List<Book>> getBooks() {
    return ResponseEntity.ok(bookRepository.findAllWithAuthor());
}

@GetMapping("/n-plus-one")
public ResponseEntity<List<Book>> getBooksNPlusOne() {
    return ResponseEntity.ok(bookRepository.findAll());
}
```

---

## 📊 SQL Comparison

### JOIN FETCH (1 Query)
```sql
select b.*, a.* from books b
join authors a on a.id = b.author_id
```

### N+1 (Multiple Queries)
```sql
select * from books           -- 1 query
select * from authors where id=?  -- N queries
```

---

## 📋 Endpoints

| Method | URL | Purpose |
|--------|-----|---------|
| GET | `/books` | JOIN FETCH (1 query) |
| GET | `/books/n-plus-one` | N+1 Demo |
| POST | `/books` | Create with auditing |
| PUT | `/books/{id}` | Update with auditing |

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| Missing `@EnableJpaAuditing` | Add to main class |
| Missing `@EntityListeners` | Add to AuditableEntity |
| No `AuditorAware` bean | Create AuditorAware |
| Using `findAll()` with lazy loading | Use JOIN FETCH |
| Outdated table schema | Drop and recreate tables |

---

## ✅ Day 6 Checklist

### Auditing
- [x] @EnableJpaAuditing enabled
- [x] AuditableEntity base class
- [x] Entities extend AuditableEntity
- [x] AuditorAware bean provided
- [x] created_at set automatically
- [x] updated_at updated automatically
- [x] created_by set automatically

### Query Optimization
- [x] Hibernate statistics enabled
- [x] N+1 detected in logs
- [x] JOIN FETCH fixed N+1
- [x] PUT endpoint uses JOIN FETCH

