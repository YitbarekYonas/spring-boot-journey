# Week 4, Day 2: Native Queries & When to Use Them

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-2-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Native%20Queries-orange.svg)]()

> **"Native queries give you full SQL power when JPQL isn't enough."**

---

## 🎯 Learning Objectives

- ✅ Understand when to use native queries vs JPQL
- ✅ Write native queries with `@Query(nativeQuery = true)`
- ✅ Use CASE expressions for conditional aggregation
- ✅ Map native query results to interface projections
- ✅ Understand the tradeoffs of native queries
- ✅ Recognize when to avoid native queries

---

## 💡 What I Learned Today

### 1. Native Query Definition
A native query is raw SQL sent directly to the database, bypassing JPQL translation.

```java
@Query(value = "SELECT * FROM books WHERE genre = :genre",
       nativeQuery = true)
List<Book> findByGenreNative(@Param("genre") String genre);
```

### 2. When to Use Native Queries
- ✅ Database-specific functions (PostgreSQL full-text search, window functions)
- ✅ Complex aggregations with CASE expressions
- ✅ Performance-critical queries needing exact SQL control
- ✅ Bulk operations bypassing persistence context

### 3. When NOT to Use Native Queries
- ❌ Things JPQL can handle
- ❌ When database portability matters
- ❌ When H2 test compatibility is needed

### 4. Return Type Options

| Type | Description |
|------|-------------|
| `List<Entity>` | Single table SELECT * |
| `List<Object[]>` | Fragile, avoid |
| Interface Projection | ✅ Clean, type-safe with column aliases |

### 5. Interface Projection
Define an interface with getter methods matching SQL column aliases:

```java
public interface LoanCountByBook {
    Long getBookId();
    String getBookTitle();
    Long getTotalLoans();
    Long getActiveLoans();
}
```

Spring creates a proxy populated from query results.

### 6. Native Query with CASE Expressions

```sql
SELECT
    b.id AS bookId,
    b.title AS bookTitle,
    COUNT(l.id) AS totalLoans,
    SUM(CASE WHEN l.status = 'ACTIVE' THEN 1 ELSE 0 END) AS activeLoans
FROM books b
LEFT JOIN loans l ON l.book_id = b.id
GROUP BY b.id, b.title
```

### 7. Decision Tree

```
Derived Query → JPQL → Native SQL
     ↓           ↓         ↓
  Simple      Complex    DB-specific
  queries     queries    functions
```

---

## 💻 Code Examples

### Interface Projection

```java
public interface LoanCountByBook {
    Long getBookId();
    String getBookTitle();
    String getBookIsbn();
    String getAuthorName();
    Long getTotalLoans();
    Long getActiveLoans();
    Long getReturnedLoans();
}
```

### Native Query with CASE

```java
@Query(value = """
    SELECT
        b.id                AS bookId,
        b.title             AS bookTitle,
        b.isbn              AS bookIsbn,
        CONCAT(a.first_name, ' ', a.last_name) AS authorName,
        COUNT(l.id)         AS totalLoans,
        SUM(CASE WHEN l.status = 'ACTIVE'   THEN 1 ELSE 0 END) AS activeLoans,
        SUM(CASE WHEN l.status = 'RETURNED' THEN 1 ELSE 0 END) AS returnedLoans
    FROM books b
    JOIN authors a ON a.id = b.author_id
    LEFT JOIN loans l ON l.book_id = b.id
    GROUP BY b.id, b.title, b.isbn, a.first_name, a.last_name
    ORDER BY totalLoans DESC
    """,
    nativeQuery = true)
List<LoanCountByBook> getLoanCountPerBook();
```

### Controller Endpoint

```java
@GetMapping("/loans/stats/per-book")
public List<LoanCountByBook> getLoanCountPerBook() {
    return loanRepository.getLoanCountPerBook();
}
```

---

## 📋 Debug Endpoints

| Endpoint | Query Type |
|----------|------------|
| `GET /api/debug/loans/stats/per-book` | Native with CASE |
| `GET /api/debug/loans/stats/per-member` | Native with CASE |
| `GET /api/debug/books/search?keyword=` | JPQL LIKE |
| `GET /api/debug/books/genre/{genre}` | JPQL WHERE |
| `GET /api/debug/books/author/{lastName}` | JPQL JOIN |

---

## 📊 Quick Reference

| Concept | My Understanding |
|---------|------------------|
| Native Query | Raw SQL sent directly to database |
| Interface Projection | Type-safe mapping via column aliases |
| CASE Expression | Conditional aggregation in SQL |
| WHEN TO USE | DB-specific functions, complex aggregations |
| WHEN TO AVOID | JPQL can handle it, portability matters |

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| Missing `nativeQuery = true` | Add parameter |
| Wrong column alias names | Match interface getter names |
| Forgetting `@Param` | Add for every named parameter |
| Using `Object[]` return | Use interface projection |
| Hardcoded values | Use named parameters |

---

## ✅ Day 2 Checklist

### Concepts
- [x] Native query vs JPQL differences
- [x] When to use native queries
- [x] Interface projections for type-safe results
- [x] CASE expressions for conditional aggregation

### Code
- [x] LoanCountByBook projection interface
- [x] MemberLoanStats projection interface
- [x] Native query with CASE expressions
- [x] QueryDebugController with endpoints
- [x] Tested in Postman

---
