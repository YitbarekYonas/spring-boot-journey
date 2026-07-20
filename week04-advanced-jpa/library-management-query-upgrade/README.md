# Week 4, Day 7: Library Management Query Layer Upgrade

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-7-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Mini%20Project-orange.svg)]()

> **"Production-ready data access: JPQL, pagination, DTOs, transactions, and auditing."**

---

## 🎯 Learning Objectives

- ✅ Apply all Week 4 concepts to a real project
- ✅ Implement pagination with PageResponse wrapper
- ✅ Write JPQL queries with JOIN FETCH and countQuery
- ✅ Create DTO projections for clean API responses
- ✅ Write native query with interface projection
- ✅ Implement atomic transaction with @Transactional
- ✅ Add auditing to all entities via AuditableEntity

---

## 💡 What I Learned Today

### 1. Week 4 Chain Summary

```
Day 1: JPQL → Day 2: Native Queries → Day 3: Pagination
→ Day 4: DTO Projections → Day 5: Transactions
→ Day 6: Auditing → Day 7: Mini-Project
```

### 2. Key Upgrades Made

| Feature | Implementation |
|---------|----------------|
| Pagination | `PageResponse<T>` wrapper + `@PageableDefault` |
| JPQL Queries | Full-text search, genre filter, DTO projection |
| Native Query | Book loan statistics with CASE expressions |
| Transactions | Atomic checkout/return with `@Transactional` |
| Auditing | `AuditableEntity` base class with timestamps |

### 3. Query Escalation Rule

```
Derived Query → JPQL → Native SQL
     ↓           ↓         ↓
  Simple      Complex   DB-specific
  queries     queries   functions
```

### 4. DTO Rule

```
One DTO per response shape, not per entity.
BookSummaryDto → list view
LoanDetailDto → loan details
BookLoanStats → native query stats
```

---

## 💻 Code Examples

### PageResponse Wrapper
```java
public class PageResponse<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean first;
    private final boolean last;
    private final boolean hasNext;
    private final boolean hasPrevious;

    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.hasNext = page.hasNext();
        this.hasPrevious = page.hasPrevious();
    }
}
```

### JPQL with Pagination
```java
@Query(
    value = """
        SELECT b FROM Book b
        JOIN FETCH b.author a
        WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(a.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """,
    countQuery = """
        SELECT COUNT(b) FROM Book b
        JOIN b.author a
        WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(a.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """
)
Page<Book> searchPaginated(@Param("keyword") String keyword, Pageable pageable);
```

### DTO Projection
```java
@Query("""
    SELECT new com.JavaBackEnd.library.dto.BookSummaryDto(
        b.id, b.title, b.isbn, b.genre, b.availableCopies,
        b.price, a.firstName, a.lastName, b.publishDate
    )
    FROM Book b
    JOIN b.author a
    WHERE b.availableCopies > 0
    ORDER BY b.title ASC
    """)
List<BookSummaryDto> findAvailableBookSummaries();
```

### Native Query with Projection
```java
@Query(value = """
    SELECT
        b.id AS bookId,
        b.title AS bookTitle,
        COUNT(l.id) AS totalLoans,
        SUM(CASE WHEN l.status = 'ACTIVE' THEN 1 ELSE 0 END) AS activeLoans
    FROM books b
    LEFT JOIN loans l ON l.book_id = b.id
    GROUP BY b.id, b.title
    ORDER BY totalLoans DESC
    """,
    nativeQuery = true)
List<BookLoanStats> getBookLoanStatistics();
```

### Atomic Transaction
```java
@Transactional
public Loan checkoutBook(Long bookId, Long memberId) {
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new IllegalArgumentException("Book not found"));

    if (!book.isAvailable()) {
        throw new IllegalStateException("No copies available");
    }

    book.checkout();  // Decrement copies
    Loan loan = new Loan(book, member, 14);
    return loanRepository.save(loan);  // Create loan
    // Both succeed or both roll back
}
```

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

---

## 📋 Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/books?page=0&size=10` | Paginated books |
| GET | `/api/books/search?keyword=clean` | Paginated search |
| GET | `/api/books/genre/{genre}` | Paginated by genre |
| GET | `/api/books/available` | DTO projection |
| GET | `/api/loans?status=ACTIVE` | Paginated loans |
| GET | `/api/loans/overdue` | Overdue loans |
| GET | `/api/loans/member/{id}` | DTO with details |
| GET | `/api/loans/stats/books` | Native query stats |
| POST | `/api/loans/checkout` | Atomic checkout |
| POST | `/api/loans/{id}/return` | Atomic return |

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| No countQuery with JOIN FETCH + Page | Add countQuery |
| Returning entities directly | Use DTOs |
| Missing @Transactional on writes | Add @Transactional |
| Overusing native queries | Use JPQL when possible |
| Manual timestamp management | Use AuditableEntity |

---

## ✅ Week 4 Mini-Project Checklist

### Auditing
- [x] AuditableEntity base class
- [x] All entities extend AuditableEntity
- [x] @EnableJpaAuditing enabled
- [x] AuditorAware bean provided

### Pagination
- [x] PageResponse<T> wrapper
- [x] @PageableDefault on all list endpoints
- [x] countQuery for JOIN FETCH queries

### JPQL Queries
- [x] Full-text search with JOIN FETCH
- [x] Genre filter with pagination
- [x] DTO projection for available books

### Native Query
- [x] Book loan statistics with CASE expressions
- [x] Interface projection mapping

### Transactions
- [x] Atomic checkout (decrement + create loan)
- [x] Atomic return (return loan + increment copies)
- [x] @Transactional on write operations

---
