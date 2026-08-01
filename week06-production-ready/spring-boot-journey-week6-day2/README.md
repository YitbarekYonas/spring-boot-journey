# Week 6, Day 2: Global Exception Handling

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-2-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Global%20Exception%20Handling-orange.svg)]()

> **"Consistent error responses turn debugging from guessing into reading."**

---

## 🎯 Learning Objectives

- ✅ Understand @ControllerAdvice and @RestControllerAdvice
- ✅ Create ErrorResponse DTO with consistent shape
- ✅ Build custom exception hierarchy
- ✅ Map exceptions to correct HTTP status codes
- ✅ Handle validation errors with field details
- ✅ Implement catch-all for unexpected exceptions

---

## 💡 What I Learned Today

### 1. The Problem Without Global Exception Handling

| Problem | Impact |
|---------|--------|
| Wrong status codes | 500 for client errors |
| No actionable info | Client doesn't know why |
| Inconsistent shape | Multiple error formats |

### 2. @RestControllerAdvice

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(LibraryException.class)
    public ResponseEntity<ErrorResponse> handleLibraryException(
            LibraryException ex, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.of(
            ex.getStatus(),
            ex.getErrorCode(),
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(ex.getStatus()).body(error);
    }
}
```

### 3. Custom Exception Hierarchy

```
LibraryException (abstract base)
    ├── ResourceNotFoundException (404)
    ├── DuplicateResourceException (409)
    └── BusinessRuleException (422)
```

### 4. ErrorResponse Shape

```json
{
    "timestamp": "2026-07-31T10:30:00",
    "status": 404,
    "error": "Not Found",
    "errorCode": "BOOK_NOT_FOUND",
    "message": "Book not found with id: 42",
    "path": "/api/books/42"
}
```

### 5. Key ErrorCodes

| ErrorCode | Status | When Used |
|-----------|--------|-----------|
| `BOOK_NOT_FOUND` | 404 | Book doesn't exist |
| `DUPLICATE_BOOK` | 409 | ISBN already exists |
| `BOOK_NOT_AVAILABLE` | 422 | Business rule violation |
| `TYPE_MISMATCH` | 400 | Invalid parameter type |
| `VALIDATION_FAILED` | 400 | @Valid constraint failure |
| `INTERNAL_ERROR` | 500 | Unexpected exception |

---

## 💻 Code Examples

### Custom Exception

```java
public class ResourceNotFoundException extends LibraryException {
    public static ResourceNotFoundException book(Long id) {
        return new ResourceNotFoundException("Book", id);
    }
}
```

### Global Exception Handler

```java
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ErrorResponse> handleNotFound(
        ResourceNotFoundException ex, HttpServletRequest request) {
    
    ErrorResponse error = ErrorResponse.of(
        ex.getStatus(),
        ex.getErrorCode(),
        ex.getMessage(),
        request.getRequestURI()
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
}
```

### Service Using Custom Exception

```java
public Book getBookById(Long id) {
    return bookRepository.findById(id)
        .orElseThrow(() -> ResourceNotFoundException.book(id));
}
```

---

## 📋 Postman Error Tests

### Environment Variables

| Variable | Value |
|----------|-------|
| `baseUrl` | `http://localhost:8080` |
| `apiVersion` | `/api` |
| `nonExistentId` | `99999` |
| `existingIsbn` | `978-0132350884` |

### Test Matrix

| Test | Method | URL | Status | ErrorCode |
|------|--------|-----|--------|-----------|
| Get Book | GET | `/books/99999` | 404 | `BOOK_NOT_FOUND` |
| Duplicate ISBN | POST | `/books` | 409 | `DUPLICATE_BOOK` |
| Type Mismatch | GET | `/books/abc` | 400 | `TYPE_MISMATCH` |
| Business Rule | POST | `/books/checkout` | 422 | `BOOK_NOT_AVAILABLE` |
| Generic Error | GET | `/test/exception` | 500 | `INTERNAL_ERROR` |

### Error Response Schema Test

```javascript
pm.test("Error response has correct schema", function() {
    const body = pm.response.json();
    pm.expect(body).to.have.property("timestamp");
    pm.expect(body).to.have.property("status");
    pm.expect(body).to.have.property("error");
    pm.expect(body).to.have.property("errorCode");
    pm.expect(body).to.have.property("message");
    pm.expect(body).to.have.property("path");
    pm.expect(body.status).to.equal(pm.response.code);
});
```

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| Returning 500 for all errors | Map to correct status |
| Exposing stack traces to client | Use generic message |
| Missing @RestControllerAdvice | Add annotation |
| Not handling validation errors | Add handler |
| Wrong handler specificity | Most specific wins |

---

## ✅ Day 2 Checklist

### Concepts
- [x] @RestControllerAdvice
- [x] Custom exception hierarchy
- [x] ErrorResponse DTO
- [x] errorCode field for client handling
- [x] Handler specificity rule

### Code
- [x] LibraryException (abstract base)
- [x] ResourceNotFoundException (404)
- [x] DuplicateResourceException (409)
- [x] BusinessRuleException (422)
- [x] GlobalExceptionHandler
- [x] ErrorResponse DTO

### Testing
- [x] 404 Not Found → BOOK_NOT_FOUND
- [x] 409 Conflict → DUPLICATE_BOOK
- [x] 400 Type Mismatch → TYPE_MISMATCH
- [x] 422 Business Rule → BOOK_NOT_AVAILABLE
- [x] 500 Internal Error → INTERNAL_ERROR

---
