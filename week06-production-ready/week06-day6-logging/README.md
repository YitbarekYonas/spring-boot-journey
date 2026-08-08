# Week 6, Day 6: Logging & Clean Code Practices

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-6-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Logging%20%26%20Clean%20Code-orange.svg)]()

> **"Good logs tell a story. Bad logs are noise. No logs are a blindfold."**

---

## 🎯 Learning Objectives

- ✅ Use `@Slf4j` instead of manual Logger declaration
- ✅ Apply the four log levels correctly (DEBUG, INFO, WARN, ERROR)
- ✅ Build a `RequestLoggingFilter` that logs every HTTP request with duration
- ✅ Use MDC (Mapped Diagnostic Context) to attach a `requestId` to every log line
- ✅ Configure Logback via `logback-spring.xml` with dev and prod profiles
- ✅ Know where to log: service layer = business events, controller = params only

---

## 💡 What I Learned Today

### 1. The Four Log Levels — When to Use Each

| Level | When to Use | Example |
|-------|-------------|---------|
| `DEBUG` | Method entry/exit, variable values, internal flow | `"getBookById() called — id=7"` |
| `INFO` | Meaningful business events | `"Book created — id=3, title='Clean Code'"` |
| `WARN` | Unexpected but handled — no action needed now | `"Book not found: id=99"` |
| `ERROR` | System failure — always investigate | `"External sync FAILED — timeout"` |

### 2. Common Logging Mistakes

| Mistake | Fix |
|---------|-----|
| `System.out.println()` anywhere | Always use `log.debug()` / `log.info()` |
| `log.error()` for 404 Not Found | 404 is expected — use `log.warn()` |
| Logging the same event twice | One log per event; don't duplicate across layers |
| Logging passwords or tokens | Never log sensitive data — only log IDs and titles |
| `log.error("msg " + ex.getMessage())` | Use `log.error("msg", ex)` — prints full stack trace |
| No log at all in service methods | Every business operation needs at least an INFO log |

### 3. MDC — Mapped Diagnostic Context

```
Without MDC — impossible to trace one request across log lines:
  08:05:02 INFO  BookService    - Fetching book id: 7
  08:05:02 INFO  BookService    - Fetching book id: 99    ← different request?
  08:05:02 WARN  BookService    - Book not found: 99

With MDC requestId — every line for a request shares the same ID:
  08:05:02 INFO  [a3f1b72c] BookService - Fetching book id: 7
  08:05:02 INFO  [b72c9d01] BookService - Fetching book id: 99
  08:05:02 WARN  [b72c9d01] BookService - Book not found: 99

grep "b72c9d01" app.log  → see the full lifecycle of that one request
```

### 4. Where to Log

| Layer | What to Log |
|-------|-------------|
| `RequestLoggingFilter` | `→ GET /api/books`, `← GET /api/books → 200 (12ms)` |
| `Controller` | Path variable values, query params — things the filter can't see |
| `Service` | All business events at INFO; method debug at DEBUG |
| `Repository` | Nothing — Hibernate's `show-sql=true` already logs queries |

---

## 💻 Code Examples

### @Slf4j — Before vs After

```java
// ❌ Before: manual Logger declaration (every class needs this line)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookService {
    private static final Logger log = LoggerFactory.getLogger(BookService.class);
}

// ✅ After: @Slf4j generates it at compile time
@Slf4j
public class BookService {
    // 'log' is available automatically — Lombok generated it
}
```

### Correct Log Level Usage in Service

```java
public Optional<Book> getBookById(Long id) {
    log.debug("getBookById() called — id={}", id);    // DEBUG: internal detail

    Optional<Book> book = bookRepository.findById(id);

    if (book.isEmpty()) {
        log.warn("Book not found: id={}", id);         // WARN: expected, handled
    }
    return book;
}

public Book createBook(CreateBookRequest request) {
    Book saved = bookRepository.save(book);
    log.info("Book created — id={}, title='{}'",      // INFO: business event
             saved.getId(), saved.getTitle());
    return saved;
}

public void syncWithExternal(Long bookId) {
    try {
        externalApi.sync(bookId);
    } catch (Exception ex) {
        log.error("Sync FAILED for book id={}", bookId, ex);  // ERROR: system failure
        throw ex;
    }
}
```

### RequestLoggingFilter — MDC in Action

```java
@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ... {

        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);              // attach to thread
        response.setHeader("X-Request-Id", requestId);

        long start = System.currentTimeMillis();
        log.info("→ {} {}", request.getMethod(), request.getRequestURI());

        try {
            chain.doFilter(request, response);
        } finally {
            log.info("← {} {} → {} ({}ms)",
                     request.getMethod(), request.getRequestURI(),
                     response.getStatus(), System.currentTimeMillis() - start);
            MDC.clear();   // ← CRITICAL: always clear after request
        }
    }
}
```

### logback-spring.xml — Pattern with MDC

```xml
<appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
        <!-- %X{requestId} reads the MDC value for every line -->
        <pattern>
            %d{HH:mm:ss.SSS} %-5level [%thread] [%X{requestId}] %logger{36} - %msg%n
        </pattern>
    </encoder>
</appender>
```

---

## 📋 Postman Tests

### Environment Variables

| Variable | Value |
|----------|-------|
| `baseUrl` | `http://localhost:8080` |
| `apiVersion` | `/api` |

### Test Cases — Watch the Console for Each

| Endpoint | Method | Expected Log Level | What to Observe |
|----------|--------|--------------------|-----------------|
| `/api/books` | GET | DEBUG + filter INFO | Filter logs → + ←, service logs page info |
| `/api/books/1` | GET | DEBUG | Service logs `"found: Clean Code"` |
| `/api/books/99` | GET | WARN | Service logs `"book not found: id=99"` |
| `/api/books/1/sync` | GET | INFO | Service logs `"External sync completed"` |
| `/api/books/2/sync` | GET | ERROR | Service logs `"External sync FAILED"` + stack trace |
| `POST /api/books` | POST | INFO | Service logs `"Book created — id=4"` |

### Console Output — GET /api/books/99 (expected)

```
08:15:33.012 INFO  [http-nio-8080-exec-3] [a3f1b72c] RequestLoggingFilter - → GET /api/books/99
08:15:33.014 DEBUG [http-nio-8080-exec-3] [a3f1b72c] BookService           - getBookById() called — id=99
08:15:33.021 WARN  [http-nio-8080-exec-3] [a3f1b72c] BookService           - getBookById() — book not found: id=99
08:15:33.023 INFO  [http-nio-8080-exec-3] [a3f1b72c] RequestLoggingFilter  - ← GET /api/books/99 → 404 (11ms)
```
All four lines share `[a3f1b72c]` — same request, tracked through the full stack.

### Console Output — GET /api/books/2/sync (error demo)

```
08:20:11.001 INFO  [http-nio-8080-exec-5] [c9d01e44] RequestLoggingFilter - → GET /api/books/2/sync
08:20:11.003 INFO  [http-nio-8080-exec-5] [c9d01e44] BookService          - Starting external sync for book id=2
08:20:11.004 ERROR [http-nio-8080-exec-5] [c9d01e44] BookService          - External sync FAILED for book id=2 — External API timeout
java.lang.RuntimeException: External API timeout
    at com.JavaBackEnd...BookService.simulateSystemFailure(BookService.java:98)
    ...
08:20:11.006 INFO  [http-nio-8080-exec-5] [c9d01e44] RequestLoggingFilter - ← GET /api/books/2/sync → 500 (5ms)
```

### POST /api/books — Sample Body

```json
{
  "title": "Design Patterns",
  "isbn": "9780201633610",
  "genre": "Software Engineering",
  "authorName": "Gang of Four",
  "price": 52.99,
  "totalCopies": 3
}
```

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| `System.out.println()` | Replace with `log.debug()` or `log.info()` |
| `log.error()` for 404 | 404 is handled — use `log.warn()` |
| `log.error(ex.getMessage())` | Use `log.error("msg", ex)` — prints stack trace |
| Logging passwords or tokens | Only log IDs, titles, non-sensitive identifiers |
| `MDC.put()` without `MDC.clear()` | Always clear in `finally` — threads are reused |
| Duplicate logs across layers | Log once at the layer that owns the decision |
| DEBUG logs in production | Set package level to INFO in prod profile |

---

## ✅ Day 6 Checklist

### Logging Setup
- [x] `@Slf4j` on every class (no manual Logger declarations)
- [x] `logback-spring.xml` with CONSOLE and FILE appenders
- [x] Profile-based log levels (DEBUG for local, INFO for prod)
- [x] MDC `requestId` in every log line

### Filter
- [x] `RequestLoggingFilter` logs `→ METHOD URI`
- [x] Response log includes status and duration `← METHOD URI → 200 (12ms)`
- [x] MDC cleared in `finally` block
- [x] `X-Request-Id` header added to response

### Service Layer Logging
- [x] `log.debug()` for method entry and internal detail
- [x] `log.info()` for business events (created, deleted)
- [x] `log.warn()` for handled edge cases (not found, duplicate)
- [x] `log.error("msg", ex)` for system failures with full stack trace

### Clean Code
- [x] Zero `System.out.println()` in codebase
- [x] No sensitive data in log statements
- [x] Controller logs only what the filter can't see
- [x] Repository layer logs nothing (Hibernate show-sql handles it)

---

**Date**: August 8, 2026
**Status**: ✅ Week 6, Day 6 Complete!
**Next**: Week 6 — Mini-Project: Production-Hardened Task Manager

> *"Logs are your eyes in production. Write them for your future self at 2am."*
