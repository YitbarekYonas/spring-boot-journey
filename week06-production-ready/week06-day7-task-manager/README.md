# Week 6, Day 7: Mini-Project — Production-Hardened Task Manager

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-7-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Mini%20Project-orange.svg)]()

> **"A production-ready API isn't just CRUD — it's CRUD with validation, meaningful errors, safe responses, and observable logs."**

---

## 🎯 What This Mini-Project Combines

| Concept | From Day | Applied Here |
|---------|----------|--------------|
| Global Exception Handling | Day 2 | `GlobalExceptionHandler` — 404, 403, 409, 400, 500 |
| Bean Validation | Day 3 | `@Valid` on all request DTOs |
| DTOs & Mapping | Day 4 | `TaskResponse.from(task)`, `Page.map()` |
| Lombok | Day 5 | `@Slf4j`, `@Builder`, `@RequiredArgsConstructor` |
| Structured Logging | Day 6 | MDC requestId, correct log levels, `RequestLoggingFilter` |

---

## 💡 What I Learned This Week (Summary)

### Exception Handling Flow

```
Controller → Service → throws TaskNotFoundException
                              ↓
                   GlobalExceptionHandler catches it
                              ↓
                   Returns consistent JSON:
                   { status: 404, error: "Not Found", message: "Task not found: id=99" }
```

### All Error Scenarios Covered

| Scenario | Exception | HTTP Status |
|----------|-----------|-------------|
| Task id doesn't exist | `TaskNotFoundException` | 404 |
| Caller is not the owner | `UnauthorizedActionException` | 403 |
| Duplicate title for same owner | `DuplicateTaskException` | 409 |
| Missing/invalid request fields | `MethodArgumentNotValidException` | 400 + fieldErrors |
| Any other crash | `Exception` | 500 |

---

## 💻 Key Code

### GlobalExceptionHandler — One Class, All Errors

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(TaskNotFoundException ex) {
        log.warn("Task not found: {}", ex.getMessage());
        return ResponseEntity.status(404).body(error(404, "Not Found", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors()
            .stream()
            .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        // Returns 400 + per-field error map
    }
}
```

### TaskResponse — Computed `overdue` Field

```java
public static TaskResponse from(Task task) {
    return TaskResponse.builder()
        .id(task.getId())
        .title(task.getTitle())
        .status(task.getStatus())
        .overdue(task.getDueDate() != null
                 && task.getDueDate().isBefore(LocalDate.now())
                 && task.getStatus() != Task.Status.DONE)  // computed, not in DB
        .build();
}
```

### Owner Check in Service

```java
public Task updateTask(Long id, String callerEmail, UpdateTaskRequest req) {
    Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));

    if (!task.getOwnerEmail().equals(callerEmail)) {
        throw new UnauthorizedActionException("Only the task owner can update this task");
    }
    // proceed with update...
}
```

---

## 📋 Postman Tests

### Environment Variables

| Variable | Value |
|----------|-------|
| `baseUrl` | `http://localhost:8080` |
| `apiVersion` | `/api` |

### Full Test Sequence

| # | Endpoint | Method | Body/Params | Expected Response |
|---|----------|--------|-------------|-------------------|
| 1 | `/api/tasks` | GET | — | 200 + Page of 4 tasks |
| 2 | `/api/tasks/by-owner?owner=alice@example.com` | GET | — | 200 + 3 tasks |
| 3 | `/api/tasks/by-status?status=TODO` | GET | — | 200 + 2 tasks |
| 4 | `/api/tasks/99` | GET | — | **404** + error JSON |
| 5 | `POST /api/tasks` | POST | `{}` (empty body) | **400** + fieldErrors |
| 6 | `POST /api/tasks` | POST | Duplicate title | **409** conflict |
| 7 | `PATCH /api/tasks/1?caller=bob@example.com` | PATCH | `{"status":"DONE"}` | **403** forbidden |
| 8 | `PATCH /api/tasks/1?caller=alice@example.com` | PATCH | `{"status":"DONE"}` | 200 + updated task |
| 9 | `DELETE /api/tasks/3?caller=alice@example.com` | DELETE | — | **403** forbidden (bob's task) |
| 10 | `DELETE /api/tasks/1?caller=alice@example.com` | DELETE | — | **204** no content |

### POST /api/tasks — Valid Body

```json
{
  "title": "Implement Docker deployment",
  "description": "Write Dockerfile and docker-compose.yml",
  "priority": "HIGH",
  "dueDate": "2026-12-01",
  "ownerEmail": "alice@example.com"
}
```

### POST /api/tasks — Validation Failure Response (400)

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed — check fieldErrors",
  "timestamp": "2026-08-08T10:30:00",
  "fieldErrors": {
    "title": "Title is required",
    "ownerEmail": "Owner email is required"
  }
}
```

### GET /api/tasks/99 — Not Found Response (404)

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Task not found: id=99",
  "timestamp": "2026-08-08T10:31:00"
}
```

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| `try-catch` in every controller | One `@RestControllerAdvice` handles all |
| `log.error()` for 404 | 404 is expected — use `log.warn()` |
| Returning entity directly | Always `TaskResponse.from(task)` |
| `@Data` on Task entity | Use `@Getter @Setter @Builder` separately |
| Forgetting `MDC.clear()` | Always clear in `finally` — threads are reused |
| Authorization check in controller | Belongs in service — controller is for HTTP only |

---

## ✅ Week 6 Final Checklist

### Exception Handling
- [x] `GlobalExceptionHandler` with `@RestControllerAdvice`
- [x] Custom exceptions (`TaskNotFoundException`, `UnauthorizedActionException`, `DuplicateTaskException`)
- [x] `MethodArgumentNotValidException` returns per-field error map
- [x] Consistent `ErrorResponse` shape across all errors

### Validation
- [x] `@Valid` on all `@RequestBody` parameters
- [x] `@NotBlank`, `@Size`, `@Email`, `@Future` on request DTOs
- [x] 400 response includes `fieldErrors` map

### DTOs
- [x] `TaskResponse` with `@Builder` — entity never returned directly
- [x] `overdue` as computed field (not stored in DB)
- [x] `Page.map(TaskResponse::from)` on all paginated endpoints

### Lombok
- [x] `@Slf4j` on all classes — zero manual Logger declarations
- [x] `@RequiredArgsConstructor` for DI
- [x] `@Builder` + targeted annotations on entity (no `@Data`)

### Logging
- [x] `RequestLoggingFilter` — `→ METHOD URI` and `← METHOD URI → STATUS (Xms)`
- [x] MDC `requestId` on every log line
- [x] Correct levels: DEBUG for internal, INFO for events, WARN for handled, ERROR for system

---
