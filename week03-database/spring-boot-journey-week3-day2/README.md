# Week 3, Day 2: Entities & Basic Mapping

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-2-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Entities%20%26%20Mapping-orange.svg)]()

> **"An entity is a Java class that Hibernate maps to a database table."**

---

## 🎯 Learning Objectives

- ✅ Define what an **entity** is in JPA
- ✅ Use `@Entity`, `@Table`, `@Id`, `@GeneratedValue`
- ✅ Map columns with `@Column` (nullable, length, unique)
- ✅ Map enums with `@Enumerated(EnumType.STRING)`
- ✅ Map dates with `LocalDate` and `LocalDateTime`
- ✅ Understand ID generation strategies (IDENTITY vs SEQUENCE)
- ✅ Set up PostgreSQL with Docker
- ✅ Verify entity mapping by inspecting generated SQL

---

## 💡 What I Learned Today

### 1. What is an Entity?
A Java class annotated with `@Entity` that maps to a database table. Each instance = a row, each field = a column.

### 2. Essential Annotations

| Annotation | Purpose |
|------------|---------|
| `@Entity` | Marks class as a JPA entity |
| `@Table(name = "tasks")` | Explicit table name |
| `@Id` | Primary key field |
| `@GeneratedValue(strategy = IDENTITY)` | Auto-increment ID |
| `@Column(nullable = false, length = 200)` | Column constraints |
| `@Enumerated(EnumType.STRING)` | Store enum as string (NOT ordinal!) |
| `@Column(updatable = false)` | Exclude from UPDATE statements |

### 3. ID Generation Strategies

| Strategy | When to Use |
|----------|-------------|
| `IDENTITY` | Most common for PostgreSQL/MySQL |
| `SEQUENCE` | Better for batch inserts in PostgreSQL |
| `UUID` | Distributed systems |

### 4. Date/Time Mapping
- `LocalDate` → `DATE` column
- `LocalDateTime` → `TIMESTAMP` column
- Never use `java.util.Date`

### 5. Enum Mapping
```java
// ❌ Fragile - stores 0, 1, 2
@Enumerated
private TaskStatus status;

// ✅ Safe - stores "TODO", "IN_PROGRESS"
@Enumerated(EnumType.STRING)
private TaskStatus status;
```

### 6. Docker PostgreSQL Setup
```bash
docker run --name taskmanager-db \
  -e POSTGRES_DB=taskmanager_dev \
  -e POSTGRES_USER=devuser \
  -e POSTGRES_PASSWORD=devpass \
  -p 5432:5432 \
  -d postgres:16
```

### 7. Important Entity Rules
- **No-arg constructor required** (make it `protected`)
- **`updatable = false`** for `createdAt` fields
- **Always use `jakarta.persistence`** (not `javax`)
- **`ddl-auto: update`** for dev, NEVER for production

---

## 💻 Code Examples

### Task Entity
```java
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Task() {}
    // ... getters/setters
}
```

### User Entity
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    protected User() {}
    // ... getters/setters
}
```

### application.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/taskmanager_dev
    username: devuser
    password: devpass
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

---

## 📊 Generated SQL

```sql
create table tasks (
    id bigserial not null,
    created_at timestamp(6) not null,
    due_date date,
    priority varchar(20) not null,
    status varchar(20) not null,
    title varchar(200) not null,
    description varchar(2000),
    updated_at timestamp(6) not null,
    primary key (id)
)

create table users (
    id bigserial not null,
    created_at timestamp(6) not null,
    email varchar(150) not null,
    is_active boolean not null,
    name varchar(100) not null,
    role varchar(20) not null,
    primary key (id)
)
```

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| `javax.persistence` | Use `jakarta.persistence` |
| Missing no-arg constructor | Add `protected Task() {}` |
| Enum ordinal storage | Use `@Enumerated(EnumType.STRING)` |
| `java.util.Date` | Use `LocalDate` / `LocalDateTime` |
| Forgetting `updatable = false` on `createdAt` | Add `@Column(updatable = false)` |
| Hardcoding table names | Use `@Table(name = "tasks")` |

---

## 🔧 Debugging Commands

```bash
# Check PostgreSQL container
docker ps

# Connect to PostgreSQL
docker exec -it taskmanager-db psql -U devuser -d taskmanager_dev

# List tables
docker exec -it taskmanager-db psql -U devuser -d taskmanager_dev -c "\dt"
```

---

## ✅ Day 2 Checklist

### Setup
- [x] PostgreSQL running in Docker on port 5432
- [x] Database `taskmanager_dev` created
- [x] User `devuser` with password `devpass`

### Code
- [x] Task entity with all annotations
- [x] TaskStatus enum
- [x] TaskPriority enum
- [x] User entity with all annotations
- [x] UserRole enum
- [x] application.yml with correct configuration

### Verification
- [x] Application starts without errors
- [x] Hibernate generates correct SQL
- [x] Tables `tasks` and `users` created
- [x] Column constraints verified (nullable, length, unique)

---
