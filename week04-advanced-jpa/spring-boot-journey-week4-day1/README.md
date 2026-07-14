# Week 4, Day 1: JPQL Basics

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-1-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-JPQL-orange.svg)]()

> **"JPQL operates on entities and fields, not tables and columns."**

---

## 🎯 Learning Objectives

- ✅ Understand JPQL vs SQL differences
- ✅ Write JPQL queries with `@Query`
- ✅ Use named parameters with `@Param`
- ✅ Write multi-condition WHERE clauses
- ✅ Write JOIN queries on relationships
- ✅ Write LIKE queries for text search
- ✅ Write aggregate queries (COUNT, AVG)
- ✅ Predict SQL before running JPQL

---

## 💡 What I Learned Today

### 1. JPQL vs SQL

| Aspect | SQL | JPQL |
|--------|-----|------|
| **Operates on** | Tables and columns | Entity classes and fields |
| **Table name** | `books` | `Book` |
| **Column name** | `available_copies` | `b.availableCopies` |
| **JOIN** | `ON a.id = b.author_id` | `JOIN b.author a` |
| **Returns** | `Object[]` | Typed entities |
| **Database** | Specific syntax | Works on any JPA-supported DB |

### 2. JPQL Query Structure

```jpql
SELECT [what to return]
FROM [Entity] [alias]
[JOIN [relationship] [alias]]
[WHERE [conditions]]
[GROUP BY [fields]]
[HAVING [conditions]]
[ORDER BY [fields] [ASC|DESC]]
```

### 3. Named Parameters (Always Use These!)

```java
@Query("SELECT b FROM Book b WHERE b.genre = :genre")
List<Book> findByGenre(@Param("genre") String genre);
```

- `:paramName` in query
- `@Param("paramName")` on method parameter
- Order doesn't matter - binds by name
- NEVER use positional parameters (`?1`, `?2`)

### 4. JPQL JOIN Types

```java
// INNER JOIN - only entities with matching relationship
@Query("SELECT b FROM Book b JOIN b.author a WHERE a.lastName = :lastName")

// LEFT JOIN - all entities, nulls for non-matching
@Query("SELECT b FROM Book b LEFT JOIN b.loans l")

// JOIN FETCH - loads relationship data in same query (prevents N+1)
@Query("SELECT b FROM Book b JOIN FETCH b.author a WHERE b.id = :id")
```

### 5. Common JPQL Operators

| Operator | Example |
|----------|---------|
| Equality | `b.genre = :genre` |
| Comparison | `b.availableCopies > :min` |
| BETWEEN | `b.price BETWEEN :min AND :max` |
| LIKE | `b.title LIKE CONCAT('%', :keyword, '%')` |
| IS NULL | `b.publishDate IS NULL` |
| IN | `b.genre IN :genres` |
| SIZE | `SIZE(a.books) > 0` |
| IS EMPTY | `a.books IS EMPTY` |

### 6. Aggregate Functions

```java
@Query("SELECT COUNT(b) FROM Book b WHERE b.genre = :genre")
long countByGenre(@Param("genre") String genre);

@Query("SELECT AVG(b.price) FROM Book b WHERE b.genre = :genre")
Double averagePriceByGenre(@Param("genre") String genre);

@Query("SELECT b.genre, COUNT(b) FROM Book b GROUP BY b.genre")
List<Object[]> countBooksPerGenre();
```

---

## 💻 Code Examples

### Multi-condition WHERE
```java
@Query("""
    SELECT b FROM Book b
    JOIN FETCH b.author a
    WHERE b.genre = :genre
    AND b.availableCopies > :minAvailable
    AND b.price <= :maxPrice
    ORDER BY b.title ASC
    """)
List<Book> findByGenreAndAvailabilityAndPriceRange(
    @Param("genre") String genre,
    @Param("minAvailable") int minAvailable,
    @Param("maxPrice") BigDecimal maxPrice
);
```

### JOIN with Filter
```java
@Query("""
    SELECT b FROM Book b
    JOIN FETCH b.author a
    WHERE a.lastName = :authorLastName
    AND b.availableCopies > 0
    ORDER BY b.title ASC
    """)
List<Book> findAvailableBooksByAuthorLastName(
    @Param("authorLastName") String authorLastName
);
```

### LIKE Search
```java
@Query("""
    SELECT b FROM Book b
    JOIN FETCH b.author a
    WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR LOWER(a.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
    ORDER BY b.title ASC
    """)
List<Book> searchByTitleOrAuthor(@Param("keyword") String keyword);
```

### Aggregate COUNT
```java
@Query("SELECT COUNT(b) FROM Book b WHERE b.genre = :genre")
long countByGenre(@Param("genre") String genre);
```

---

## 📊 Query Verification Process

**Step 1 - Predict SQL before running**

**Step 2 - Run query and read actual SQL in console**

**Step 3 - Compare prediction vs actual**

### Example Predictions:

| JPQL | Predicted SQL |
|------|---------------|
| `SELECT b FROM Book b JOIN FETCH b.author a WHERE b.genre = :genre` | `SELECT b.*, a.* FROM books b INNER JOIN authors a ON a.id = b.author_id WHERE b.genre = ?` |
| `SELECT COUNT(b) FROM Book b WHERE b.genre = :genre` | `SELECT COUNT(*) FROM books WHERE genre = ?` |

---

## 📋 Debug Endpoints

| Endpoint | JPQL Query |
|----------|------------|
| `GET /api/debug/books/search?keyword=clean` | LIKE search |
| `GET /api/debug/books/genre/Software%20Engineering` | Multi-condition |
| `GET /api/debug/books/author/Martin` | JOIN filter |
| `GET /api/debug/books/stats/genre-counts` | Aggregate COUNT |
| `GET /api/debug/books/on-loan` | JOIN with filter |

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| Using table name instead of entity | `Book` not `books` |
| Using column name instead of field | `b.availableCopies` not `available_copies` |
| Missing `@Param` | Add `@Param` for every named parameter |
| Using positional parameters | Use named parameters `:name` |
| Forgetting `JOIN FETCH` | Causing N+1 problems |
| Not using `DISTINCT` | Duplicate results from JOINs |

---

## ✅ Day 1 Checklist

### Concepts
- [x] JPQL vs SQL differences
- [x] Named parameters with `@Param`
- [x] Multi-condition WHERE clauses
- [x] JOIN on entity relationships
- [x] LIKE for text search
- [x] Aggregate functions (COUNT, AVG)

### Code
- [x] BookRepository with JPQL queries
- [x] LoanRepository with JPQL queries
- [x] QueryDebugController
- [x] Predicted SQL before running
- [x] Verified SQL in console logs

---
