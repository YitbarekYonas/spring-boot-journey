# Week 6, Day 4: DTOs & Mapping

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-4-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-DTOs%20%26%20Mapping-orange.svg)]()

> **"DTOs decouple your API from your database schema."**

---

## 🎯 Learning Objectives

- ✅ Create Response DTOs (UserResponse, BookResponse)
- ✅ Use static factory method `from(entity)`
- ✅ Map entities to DTOs in controllers
- ✅ Use `Page.map()` for paginated DTOs
- ✅ Remove sensitive fields from responses
- ✅ Create summary DTOs for list endpoints

---

## 💡 What I Learned Today

### 1. Why Entities Should Never Leave Service Layer

| Problem | Impact |
|---------|--------|
| Password hash exposed | Security risk |
| Internal flags exposed | Reveals security model |
| Over-fetching | Unnecessary data transfer |
| Tight coupling | API breaks on DB changes |
| Lazy loading traps | LazyInitializationException |

### 2. DTO Types

| Type | Purpose | Example |
|------|---------|---------|
| Request DTO | Incoming data (validated) | CreateBookRequest |
| Response DTO | Outgoing data (controlled) | UserResponse |
| Internal DTO | Inter-layer transfer | BookSummaryDto |

### 3. Static Factory Method Pattern

```java
public class UserResponse {
    public static UserResponse from(User user) {
        return new UserResponse(user);
    }
    
    private UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.enabled = user.isEnabled();
        this.createdAt = user.getCreatedAt();
        // ❌ No password!
        // ❌ No internal flags!
    }
}
```

### 4. Page Mapping

```java
Page<Book> page = bookRepository.findAllWithAuthor(pageable);
Page<BookSummaryResponse> dtoPage = page.map(BookSummaryResponse::from);
return ResponseEntity.ok(new PageResponse<>(dtoPage));
```

---

## 💻 Code Examples

### UserResponse DTO

```java
public class UserResponse {
    private final Long id;
    private final String name;
    private final String email;
    private final UserRole role;
    private final boolean enabled;
    private final LocalDateTime createdAt;

    public static UserResponse from(User user) {
        return new UserResponse(user);
    }

    private UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.enabled = user.isEnabled();
        this.createdAt = user.getCreatedAt();
        // ❌ NO password, NO internal flags
    }
}
```

### BookSummaryResponse (List View)

```java
public class BookSummaryResponse {
    private final Long id;
    private final String title;
    private final String isbn;
    private final String genre;
    private final BigDecimal price;
    private final int availableCopies;
    private final String authorFullName;  // Computed field

    public static BookSummaryResponse from(Book book) {
        return new BookSummaryResponse(book);
    }

    private BookSummaryResponse(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.isbn = book.getIsbn();
        this.genre = book.getGenre();
        this.price = book.getPrice();
        this.availableCopies = book.getAvailableCopies();
        this.authorFullName = book.getAuthor() != null
            ? book.getAuthor().getFirstName() + " " + book.getAuthor().getLastName()
            : null;
    }
}
```

### BookResponse (Detail View)

```java
public class BookResponse {
    private final Long id;
    private final String title;
    private final String isbn;
    private final String genre;
    private final BigDecimal price;
    private final int totalCopies;
    private final int availableCopies;
    private final LocalDate publishDate;
    private final LocalDateTime createdAt;
    private final AuthorResponse author;

    public static BookResponse from(Book book) {
        return new BookResponse(book);
    }

    private BookResponse(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.isbn = book.getIsbn();
        this.genre = book.getGenre();
        this.price = book.getPrice();
        this.totalCopies = book.getTotalCopies();
        this.availableCopies = book.getAvailableCopies();
        this.publishDate = book.getPublishDate();
        this.createdAt = book.getCreatedAt();
        this.author = book.getAuthor() != null
            ? AuthorResponse.from(book.getAuthor())
            : null;
    }
}
```

### Controller Using DTOs

```java
@GetMapping
public ResponseEntity<PageResponse<BookSummaryResponse>> getAllBooks(
        Pageable pageable) {
    Page<Book> page = bookRepository.findAllWithAuthor(pageable);
    Page<BookSummaryResponse> dtoPage = page.map(BookSummaryResponse::from);
    return ResponseEntity.ok(new PageResponse<>(dtoPage));
}

@GetMapping("/{id}")
public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
    return bookRepository.findByIdWithAuthor(id)
        .map(BookResponse::from)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
}

@GetMapping("/me")
public ResponseEntity<UserResponse> getCurrentUser(
        @AuthenticationPrincipal CustomUserDetails currentUser) {
    return ResponseEntity.ok(UserResponse.from(currentUser.getUser()));
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

| Endpoint | Method | Response DTO | Expected Fields |
|----------|--------|--------------|-----------------|
| `/books` | GET | BookSummaryResponse | 7 fields (no totalCopies, no author entity) |
| `/books/1` | GET | BookResponse | 10 fields + nested AuthorResponse |
| `/auth/me` | GET | UserResponse | 6 fields (NO password!) |

### GET /auth/me Response

```json
{
    "id": 1,
    "name": "Admin User",
    "email": "admin@example.com",
    "role": "ADMIN",
    "enabled": true,
    "createdAt": "2026-08-04T22:00:00"
}
```
**No password, no internal flags!**

### GET /books Response

```json
{
    "content": [
        {
            "id": 1,
            "title": "Clean Code",
            "isbn": "978-0132350884",
            "genre": "Software Engineering",
            "price": 38.99,
            "availableCopies": 3,
            "authorFullName": "Robert Martin"
        }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 3,
    "totalPages": 1,
    "first": true,
    "last": true,
    "hasNext": false,
    "hasPrevious": false
}
```

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| Returning entities directly | Always use DTOs |
| Password in response | Exclude from UserResponse |
| Lazy loading in DTO mapping | Use JOIN FETCH in repository |
| Mapping outside transaction | Map inside transaction (or load all data first) |
| One DTO for all responses | Use summary DTO for lists, full DTO for detail |
| Manual DTO construction | Use static factory method `from(entity)` |

---

## ✅ Day 4 Checklist

### DTOs Created
- [x] UserResponse (no password)
- [x] BookSummaryResponse (list view)
- [x] BookResponse (detail view)
- [x] AuthorResponse (nested)
- [x] PageResponse (wrapper)

### Controllers Updated
- [x] /api/books returns BookSummaryResponse
- [x] /api/books/{id} returns BookResponse
- [x] /api/auth/me returns UserResponse

### Mappings
- [x] Static factory method `from(entity)`
- [x] Page.map(BookResponse::from)
- [x] JOIN FETCH for relationships

### Testing
- [x] No password field in responses
- [x] GET /auth/me returns 6 fields
- [x] GET /books uses summary DTO
- [x] BookSummaryResponse has authorFullName

---
