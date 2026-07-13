# Week 3, Day 7: Mini-Project - Library Management System

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-7-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Mini%20Project-orange.svg)]()

> **"A complete persistent layer with relationships, queries, and atomic operations."**

---

## 🎯 Learning Objectives

- ✅ Build a complete domain with 4 entities and relationships
- ✅ Implement One-to-Many (Author → Book) with orphanRemoval
- ✅ Implement Many-to-One (Book → Author) with proper FK mapping
- ✅ Implement Many-to-Many through join entity (Book ↔ Member via Loan)
- ✅ Create 8+ derived query methods per repository
- ✅ Use JOIN FETCH to prevent N+1 problems
- ✅ Implement atomic multi-step operations with @Transactional
- ✅ Create idempotent data seeder
- ✅ Use business keys for equals/hashCode
- ✅ Handle LazyInitializationException with @JsonIgnore

---

## 💡 What I Learned Today

### 1. Domain Relationships

| Relationship | Mapping | Owning Side |
|--------------|---------|-------------|
| Author → Book | `@OneToMany(mappedBy = "author")` | Book (has FK) |
| Book → Author | `@ManyToOne @JoinColumn` | Book |
| Book → Loan | `@OneToMany(mappedBy = "book")` | Loan (has FK) |
| Member → Loan | `@OneToMany(mappedBy = "member")` | Loan (has FK) |
| Loan → Book | `@ManyToOne @JoinColumn` | Loan |
| Loan → Member | `@ManyToOne @JoinColumn` | Loan |

### 2. orphanRemoval = true
When a child is removed from the parent's collection, it's automatically deleted from the database. Use when children can't exist without parent.

### 3. Atomic Operations with @Transactional

**Checkout Flow (All or Nothing):**
```
1. Validate book exists and is available
2. Validate member exists and is active
3. Check member hasn't already borrowed this book
4. Decrement available copies
5. Create loan record
```

**If any step fails → everything rolls back** - no partial state in database.

### 4. Business Methods on Entities

```java
book.checkout()        // Decrements available copies
book.returnCopy()      // Increments available copies
loan.returnBook()      // Sets status to RETURNED
loan.markOverdue()     // Marks as overdue
```

Logic that operates on a single entity belongs on the entity itself.

### 5. JOIN FETCH for N+1 Prevention

```java
@Query("SELECT b FROM Book b JOIN FETCH b.author")
List<Book> findAllWithAuthor();  // 1 query instead of 1+N
```

### 6. Idempotent Seeder

```java
@PostConstruct
@Transactional
public void seed() {
    if (authorRepository.count() > 0) {
        return;  // Already seeded - safe to run multiple times
    }
    // Seed data only once
}
```

### 7. @JsonIgnore to Prevent Lazy Loading

```java
@JsonIgnore
@OneToMany(mappedBy = "author")
private List<Book> books;
```

Prevents lazy loading during JSON serialization.

---

## 💻 Code Structure

### Entities

```java
Author (1) ──→ (Many) Book
Book   (1) ──→ (Many) Loan
Member (1) ──→ (Many) Loan
```

### Repositories

| Repository | Query Methods |
|------------|---------------|
| AuthorRepository | `findByEmail`, `findByLastNameContaining`, `existsByEmail`, `findByIdWithBooks`, `findAuthorsWithBooks` |
| BookRepository | `findByIsbn`, `findByGenre`, `findByTitleContaining`, `findByAvailableCopiesGreaterThan`, `findAllWithAuthor`, `findAvailableBooksWithAuthor`, `searchByTitleOrAuthor`, `findByAuthorId` |
| MemberRepository | `findByEmail`, `existsByEmail`, `findByActive`, `findByNameContaining`, `countByActive` |
| LoanRepository | `findByStatus`, `findByMemberId`, `findByBookId`, `existsByBookIdAndMemberIdAndStatus`, `countByStatus`, `findOverdueLoans`, `findByMemberIdWithBookAndAuthor` |

### Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/books` | All books with authors |
| GET | `/api/books/{id}` | Book by ID |
| GET | `/api/books/search?keyword=` | Search by title/author |
| GET | `/api/books/available` | Available books |
| GET | `/api/books/genre/{genre}` | Books by genre |
| POST | `/api/books?authorId=` | Create book |
| PUT | `/api/books/{id}` | Update book |
| GET | `/api/members` | All members |
| GET | `/api/members/active` | Active members |
| POST | `/api/members` | Create member |
| PUT | `/api/members/{id}` | Update member |
| DELETE | `/api/members/{id}` | Deactivate member |
| GET | `/api/loans` | All loans |
| GET | `/api/loans/overdue` | Overdue loans |
| GET | `/api/loans/member/{memberId}` | Member's loans |
| POST | `/api/loans/checkout?bookId=&memberId=` | Checkout book |
| POST | `/api/loans/{loanId}/return` | Return book |
| POST | `/api/loans/mark-overdue` | Mark overdue loans |

---

## 📊 Key Takeaways

| Concept | My Understanding |
|---------|------------------|
| **orphanRemoval** | Deletes children automatically when removed from parent |
| **@Transactional** | All operations succeed or all roll back |
| **JOIN FETCH** | Loads relationships in one query (prevents N+1) |
| **Idempotent Seeder** | Safe to run multiple times |
| **Business Methods** | Entity logic belongs on the entity |
| **@JsonIgnore** | Prevents lazy loading during serialization |
| **Atomic Checkout** | Validate book, validate member, decrement copy, create loan |

---

## ✅ Day 7 Checklist

### Setup
- [x] PostgreSQL container `library-db` running
- [x] Database `library_dev` created
- [x] application.yml with dev profile

### Entities
- [x] Author entity with @OneToMany(books)
- [x] Book entity with @ManyToOne(author)
- [x] Member entity
- [x] Loan entity (join between Book and Member)
- [x] LoanStatus enum

### Repositories
- [x] AuthorRepository with 5+ methods
- [x] BookRepository with 8+ methods
- [x] MemberRepository with 5+ methods
- [x] LoanRepository with 8+ methods
- [x] JOIN FETCH queries to prevent N+1

### Services
- [x] BookService with CRUD + search
- [x] MemberService with CRUD + deactivate
- [x] LoanService with checkout, return, markOverdue
- [x] @Transactional on write operations

### Testing
- [x] Seeder runs successfully
- [x] GET /api/books returns JSON (no LazyInitializationException)
- [x] Checkout creates loan and decrements copies
- [x] Return updates loan and increments copies
- [x] All Postman endpoints work

---
