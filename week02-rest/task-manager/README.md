# Week 2, Day 7: Mini-Project - In-Memory Task Manager API

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-7-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Mini%20Project-orange.svg)]()

> **"Full CRUD REST API with proper layered architecture."**

---

## 🎯 Learning Objectives

- ✅ Build complete REST API with all CRUD operations
- ✅ Implement layered architecture (Controller → Service → Repository)
- ✅ Use enums for type-safe status and priority
- ✅ Add filtering via query parameters
- ✅ Implement state machine for status transitions
- ✅ Use proper HTTP status codes (201, 204, 404, 400)
- ✅ Test with Postman collection (chaining + automated tests)

---

## 💡 What I Learned Today

### 1. Complete REST API Structure

```
GET    /api/tasks                 → List all tasks
GET    /api/tasks?status=TODO     → Filter by status
GET    /api/tasks?priority=HIGH   → Filter by priority
GET    /api/tasks/overdue         → Get overdue tasks
GET    /api/tasks/{id}            → Get single task
POST   /api/tasks                 → Create task
PUT    /api/tasks/{id}            → Update task
PATCH  /api/tasks/{id}/status     → Update status only
DELETE /api/tasks/{id}            → Delete task
```

### 2. Status State Machine (Service Layer)

```
TODO → IN_PROGRESS → DONE
TODO → CANCELLED
IN_PROGRESS → DONE
IN_PROGRESS → CANCELLED
IN_PROGRESS → TODO
DONE → ❌ terminal state
CANCELLED → ❌ terminal state
```

### 3. Business Rules in Service

```java
// Title cannot be blank
if (task.getTitle() == null || task.getTitle().isBlank()) {
    throw new IllegalArgumentException("Title cannot be blank");
}

// Cannot delete IN_PROGRESS task
if (task.getStatus() == TaskStatus.IN_PROGRESS) {
    throw new IllegalStateException("Cannot delete IN_PROGRESS task");
}

// Valid status transitions only
validateStatusTransition(current, next);
```

### 4. Layered Architecture Applied

```
Controller → HTTP translation only
Service → Business logic, rules, state machine
Repository → Data access only
```

### 5. Postman Chaining

```javascript
// POST creates task and saves ID
pm.test("Save taskId for chaining", function() {
    const task = pm.response.json();
    pm.environment.set("taskId", task.id);
});

// Subsequent requests use {{taskId}}
GET {{baseUrl}}/api/tasks/{{taskId}}
PUT {{baseUrl}}/api/tasks/{{taskId}}
DELETE {{baseUrl}}/api/tasks/{{taskId}}
```

---

## 🔑 Key Concepts

| Concept | My Understanding |
|---------|------------------|
| **Full CRUD** | Create, Read, Update, Delete operations |
| **Enums** | Type-safe status and priority (no magic strings) |
| **State Machine** | Valid status transitions enforced in service |
| **Query Params** | Filtering via `?status=TODO&priority=HIGH` |
| **Thread Safety** | `CopyOnWriteArrayList` + `AtomicLong` for in-memory |
| **Postman Chaining** | POST saves ID, subsequent requests use it |
| **HTTP Status** | 201 Created, 204 No Content, 404 Not Found |

---

## 💻 Code Structure

```
com.JavaBackEnd.taskmanager/
├── entity/
│   ├── Task.java
│   ├── TaskStatus.java    (enum)
│   └── TaskPriority.java  (enum)
├── repository/
│   └── TaskRepository.java
├── service/
│   ├── TaskService.java   (interface)
│   └── impl/
│       └── TaskServiceImpl.java
├── controller/
│   └── TaskController.java
└── config/
    └── CorsConfig.java
```

---

## 📊 API Endpoints Summary

| Method | URL | Description | Status Codes |
|--------|-----|-------------|--------------|
| GET | `/api/tasks` | Get all tasks | 200 |
| GET | `/api/tasks?status=TODO` | Filter by status | 200 |
| GET | `/api/tasks?priority=HIGH` | Filter by priority | 200 |
| GET | `/api/tasks/overdue` | Get overdue tasks | 200 |
| GET | `/api/tasks/{id}` | Get task by ID | 200, 404 |
| POST | `/api/tasks` | Create task | 201, 400 |
| PUT | `/api/tasks/{id}` | Update task | 200, 404, 400 |
| PATCH | `/api/tasks/{id}/status` | Update status | 200, 404, 400 |
| DELETE | `/api/tasks/{id}` | Delete task | 204, 404, 400 |

---

## ✅ Checklist

### Concepts
- [x] Full CRUD REST API
- [x] Enums for status and priority
- [x] Status state machine in service
- [x] Layered architecture (Controller → Service → Repository)
- [x] Query parameter filtering
- [x] Proper HTTP status codes
- [x] Global CORS configuration

### Postman
- [x] All endpoints saved as requests
- [x] Tests tab for each request
- [x] Request chaining (POST saves ID)
- [x] Environment variables ({{baseUrl}}, {{taskId}})
- [x] Collection Runner passes all tests
- [x] Exported and committed to repo

---

## ❌ Common Mistakes

### Mistake 1: `/overdue` after `/{id}`
```java
// ❌ WRONG - "overdue" matched as ID
@GetMapping("/{id}")
public Task getById(@PathVariable Long id) { }

@GetMapping("/overdue")  // Never reached!
public List<Task> getOverdue() { }

// ✅ CORRECT - Literal before variable
@GetMapping("/overdue")  // Checked first
public List<Task> getOverdue() { }

@GetMapping("/{id}")     // Checked second
public Task getById(@PathVariable Long id) { }
```

### Mistake 2: Business Logic in Controller
```java
// ❌ WRONG
if (task.getStatus() == TaskStatus.DONE) {
    return ResponseEntity.badRequest().build();  // Business rule in controller!
}

// ✅ CORRECT - In service
throw new IllegalArgumentException("Cannot create DONE task");
```

### Mistake 3: Not Using Enums
```java
// ❌ WRONG - Magic strings
private String status = "TODO";
private String priority = "HIGH";

// ✅ CORRECT - Type-safe enums
private TaskStatus status = TaskStatus.TODO;
private TaskPriority priority = TaskPriority.HIGH;
```
