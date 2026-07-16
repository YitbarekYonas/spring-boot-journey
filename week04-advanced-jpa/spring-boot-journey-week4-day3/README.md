# Week 4, Day 3: Pagination & Sorting

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-3-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Pagination%20%26%20Sorting-orange.svg)]()

> **"Pagination prevents your API from loading thousands of rows at once."**

---

## 🎯 Learning Objectives

- ✅ Understand why pagination is essential for production APIs
- ✅ Use `Pageable` and `PageRequest` for pagination
- ✅ Return `Page<T>` with metadata
- ✅ Use `@PageableDefault` for default pagination values
- ✅ Validate sort fields with whitelist
- ✅ Fix `LazyInitializationException` with `JOIN FETCH`
- ✅ Use `countQuery` for paginated queries with fetch joins

---

## 💡 What I Learned Today

### 1. Why Pagination Matters
Without pagination, loading 50,000 books causes:
- Memory overload in JVM heap
- Slow JSON serialization
- Network timeout
- Browser freeze

### 2. Core Classes

| Class | Purpose |
|-------|---------|
| `Pageable` | Carries page number, page size, sort |
| `PageRequest` | Creates Pageable instances |
| `Page<T>` | Contains content + metadata |
| `Slice<T>` | Lighter alternative (no count query) |

### 3. Pageable Defaults

```java
@PageableDefault(size = 10, sort = "title", direction = Sort.Direction.ASC)
Pageable pageable
```

### 4. Query Parameters

```
GET /api/books?page=0&size=10&sort=title,asc
GET /api/books?page=1&size=5&sort=price,desc
GET /api/books?page=0&size=10&sort=genre,asc&sort=title,asc
```

### 5. JOIN FETCH with Pagination

```java
@Query(
    value = "SELECT b FROM Book b JOIN FETCH b.author a",
    countQuery = "SELECT COUNT(b) FROM Book b"
)
Page<Book> findAllWithAuthor(Pageable pageable);
```

### 6. LazyInitializationException Fix
- Problem: Hibernate session closes before JSON serialization
- Fix: Use `JOIN FETCH` to load relationships eagerly

### 7. Security: Validate Sort Fields

```java
Set<String> allowedSortFields = Set.of("title", "price", "genre", "publishDate");
if (!allowedSortFields.contains(sortProperty)) {
    sortBy = "title";  // Fallback to default
}
```

---

## 💻 Code Examples

### Repository with Pagination

```java
@Query(
    value = """
        SELECT b FROM Book b
        JOIN FETCH b.author a
        WHERE b.availableCopies > 0
        """,
    countQuery = """
        SELECT COUNT(b) FROM Book b
        WHERE b.availableCopies > 0
        """
)
Page<Book> findAvailableBooksWithAuthor(Pageable pageable);
```

### Controller

```java
@GetMapping
public ResponseEntity<Page<Book>> getAllBooks(
        @PageableDefault(size = 10, sort = "title") Pageable pageable) {

    // Cap page size
    if (pageable.getPageSize() > 100) {
        pageable = PageRequest.of(pageable.getPageNumber(), 100);
    }

    // Validate sort fields
    Set<String> allowed = Set.of("title", "price", "genre");
    if (!allowed.contains(getSortField(pageable))) {
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("title"));
    }

    return ResponseEntity.ok(bookRepository.findAllWithAuthor(pageable));
}
```

### Page Response

```json
{
    "content": [...],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 10,
        "sort": { "sorted": true }
    },
    "totalPages": 2,
    "totalElements": 15,
    "last": false,
    "first": true,
    "size": 10,
    "number": 0,
    "numberOfElements": 10,
    "empty": false
}
```

---

## 📋 Endpoints

| Endpoint | Method | Query Params |
|----------|--------|--------------|
| `/books` | GET | `page`, `size`, `sort` |
| `/books/available` | GET | `page`, `size` |
| `/books/search?keyword=` | GET | `keyword`, `page`, `size` |
| `/books/genre/{genre}` | GET | `page`, `size` |
| `/books/{id}` | GET | None |

---

## 📊 SQL Output

```sql
-- Data query with LIMIT/OFFSET
select b.*, a.* from books b
join authors a on a.id = b.author_id
order by b.title asc
limit 2 offset 0

-- Count query
select count(b.id) from books b
```

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| No `JOIN FETCH` | LazyInitializationException |
| Missing `countQuery` | Hibernate warning |
| Invalid sort field | PropertyReferenceException |
| No page size cap | Client requests 10,000 items |
| Page 1 as first page | Page numbers are 0-indexed |

---

## ✅ Day 3 Checklist

### Concepts
- [x] Pagination prevents memory issues
- [x] Page vs Slice difference
- [x] JOIN FETCH with countQuery
- [x] Sort field validation
- [x] LazyInitializationException fix

### Code
- [x] findAllWithAuthor with JOIN FETCH
- [x] findByIdWithAuthor with JOIN FETCH
- [x] Paginated search, genre, available queries
- [x] Controller with @PageableDefault
- [x] Sort field whitelist validation

### Testing
- [x] Page 0 returns first page
- [x] Page 1 returns second page
- [x] Sorting works for all fields
- [x] Beyond last page returns empty content
- [x] Book by ID returns with author
- [x] No LazyInitializationException

---
