# Week 4, Day 4: Projections & DTO Queries

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-4-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Projections%20%26%20DTOs-orange.svg)]()

> **"DTOs decouple your API from your database schema."**

---

## 🎯 Learning Objectives

- ✅ Understand the problems with returning entities directly
- ✅ Create DTO classes for API responses
- ✅ Use JPQL constructor expressions
- ✅ Compare SQL generated for entities vs DTOs
- ✅ Write plain Java unit tests for DTOs

---

## 💡 What I Learned Today

### 1. Problems with Returning Entities

| Problem | Impact |
|---------|--------|
| **Over-fetching** | Returns unnecessary columns |
| **Security Leakage** | Sensitive fields exposed |
| **Tight Coupling** | API tied to database schema |

### 2. DTO (Data Transfer Object)
A plain Java class that defines exactly what data to return to the client.

### 3. JPQL Constructor Expression

```java
@Query("""
    SELECT new com.example.dto.BookSummaryDto(
        b.id,
        b.title,
        b.genre,
        b.availableCopies,
        b.price,
        a.firstName,
        a.lastName
    )
    FROM Book b
    JOIN b.author a
    """)
List<BookSummaryDto> findBookSummaries();
```

### 4. Full Entity vs DTO Comparison

| Aspect | Full Entity | DTO |
|--------|-------------|-----|
| **Columns selected** | All (9+) | Only needed (7) |
| **Network payload** | Large | Small |
| **Security** | Risk | Safe |
| **Coupling** | Tight | Loose |

### 5. DTO Advantages
- ✅ Only selected columns in SQL
- ✅ No lazy loading issues
- ✅ Easy to test with `new`
- ✅ Computed fields in plain Java
- ✅ API independent of schema

---

## 💻 Code Examples

### DTO Class

```java
public class BookSummaryDto {
    private final Long id;
    private final String title;
    private final String genre;
    private final int availableCopies;
    private final BigDecimal price;
    private final String authorFirstName;
    private final String authorLastName;

    public BookSummaryDto(Long id, String title, String genre,
                          int availableCopies, BigDecimal price,
                          String authorFirstName, String authorLastName) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.availableCopies = availableCopies;
        this.price = price;
        this.authorFirstName = authorFirstName;
        this.authorLastName = authorLastName;
    }

    public String getAuthorFullName() {
        return authorFirstName + " " + authorLastName;
    }

    public boolean isAvailable() {
        return availableCopies > 0;
    }
}
```

### Repository with DTO Query

```java
@Query("""
    SELECT new com.example.dto.BookSummaryDto(
        b.id, b.title, b.genre, b.availableCopies, b.price,
        a.firstName, a.lastName
    )
    FROM Book b
    JOIN b.author a
    WHERE b.availableCopies > 0
    ORDER BY b.title ASC
    """)
List<BookSummaryDto> findAvailableBookSummaries();
```

### Paginated DTO

```java
@Query(
    value = """
        SELECT new com.example.dto.BookSummaryDto(
            b.id, b.title, b.genre, b.availableCopies, b.price,
            a.firstName, a.lastName
        )
        FROM Book b
        JOIN b.author a
        """,
    countQuery = "SELECT COUNT(b) FROM Book b"
)
Page<BookSummaryDto> findAllBookSummaries(Pageable pageable);
```

### Unit Test

```java
@Test
void testDto() {
    BookSummaryDto dto = new BookSummaryDto(
        1L, "Clean Code", "Software Engineering",
        2, new BigDecimal("38.99"), "Robert", "Martin"
    );
    
    assertEquals("Robert Martin", dto.getAuthorFullName());
    assertTrue(dto.isAvailable());
}
```

---

## 📊 SQL Comparison

### Full Entity (9+ columns)

```sql
select b.id, b.title, b.isbn, b.publish_date,
       b.price, b.total_copies, b.available_copies,
       b.genre, b.created_at, b.author_id
from books b
```

### DTO (7 columns only)

```sql
select b.id, b.title, b.genre,
       b.available_copies, b.price,
       a.first_name, a.last_name
from books b
join authors a on a.id = b.author_id
```

---

## 📋 Endpoints

| Endpoint | Method | Returns |
|----------|--------|---------|
| `/books/full` | GET | Full entity (JOIN FETCH) |
| `/books/summary` | GET | DTO (7 columns) |
| `/books` | GET | Paginated DTO |

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| Missing `JOIN FETCH` on full entity | Add JOIN FETCH |
| Wrong constructor parameter order | Match SELECT order |
| Using entity directly in API | Always use DTO |
| No `countQuery` for pagination | Add countQuery |

---

## ✅ Day 4 Checklist

### Concepts
- [x] Problems with returning entities (over-fetching, security, coupling)
- [x] DTO pattern
- [x] JPQL constructor expressions
- [x] DTO advantages

### Code
- [x] BookSummaryDto class
- [x] DTO query with constructor expression
- [x] Paginated DTO query with countQuery
- [x] JOIN FETCH for full entity
- [x] Unit test for DTO

### Testing
- [x] GET /books/full works
- [x] GET /books/summary returns DTO
- [x] GET /books paginated DTO
- [x] Compare SQL columns in logs
- [x] DTO unit tests pass

