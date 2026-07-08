# Week 3, Day 1: JDBC → JPA → Hibernate Stack

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-1-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-JDBC%20to%20JPA%20Stack-orange.svg)]()

> **"Understanding the stack means knowing what happens under the hood."**

---

## 🎯 Learning Objectives

- ✅ Understand the Object-Relational Impedance Mismatch
- ✅ Explain what JDBC is and why it's painful
- ✅ Define JPA as a specification
- ✅ Understand Hibernate as the JPA implementation
- ✅ Explain Spring Data JPA's role
- ✅ Set up PostgreSQL with Docker
- ✅ Configure Spring Boot to connect to PostgreSQL

---

## 💡 What I Learned Today

### 1. The Object-Relational Impedance Mismatch

**Problem:** Java thinks in **objects**, databases think in **tables** and **rows**.

```
Objects          → Tables
─────────────────────────────────────────────────
References       → Foreign keys (integers)
Inheritance      → No direct equivalent
Encapsulation    → All columns are equally visible
Graph of objects → Normalized rows across tables
```

### 2. The Four Layers of the Stack

| Layer | Purpose |
|-------|---------|
| **JDBC** | Raw database connection, manual SQL and mapping |
| **JPA** | Specification (annotations and interfaces only) |
| **Hibernate** | JPA implementation (does the actual work) |
| **Spring Data JPA** | Abstraction over Hibernate (JpaRepository) |

### 3. Raw JDBC is Painful

```java
// Everything manual - SQL strings, ResultSet mapping, transactions
String sql = "INSERT INTO tasks (title, status) VALUES (?, ?)";
PreparedStatement stmt = connection.prepareStatement(sql);
stmt.setString(1, task.getTitle());
stmt.setString(2, task.getStatus().name());
// ... and mapping the ResultSet back to objects
```

### 4. JPA Solves This

```java
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
}
```

### 5. Hibernate Does the Work

Hibernate:
- Generates SQL automatically
- Maps ResultSets to objects
- Handles relationships (@OneToMany, @ManyToOne)
- Provides the Persistence Context (first-level cache)
- Performs dirty checking

### 6. Spring Data JPA Simplifies Further

```java
// Just extend JpaRepository - get CRUD for free!
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);
}
```

### 7. Docker for PostgreSQL

```bash
docker run --name taskmanager-db \
  -e POSTGRES_DB=taskmanager_dev \
  -e POSTGRES_USER=devuser \
  -e POSTGRES_PASSWORD=devpass \
  -p 5432:5432 \
  -d postgres:16
```

### 8. Application Configuration

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/taskmanager_dev
    username: devuser
    password: devpass
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

### 9. The Full Call Chain

```
JpaRepository method call
         ↓
Spring Data proxy generates JPQL
         ↓
Hibernate translates JPQL → SQL
         ↓
Hibernate sends SQL to JDBC
         ↓
JDBC sends to PostgreSQL via HikariCP pool
         ↓
PostgreSQL executes query → returns ResultSet
         ↓
Hibernate maps ResultSet to objects
         ↓
List<T> returned to your service
```

---

## 🔑 Key Concepts

| Concept | My Understanding |
|---------|------------------|
| **JDBC** | Lowest level - raw SQL, manual mapping |
| **JPA** | Specification - annotations and interfaces only |
| **Hibernate** | JPA implementation - does the actual work |
| **Spring Data JPA** | Abstraction - extends JpaRepository for free CRUD |
| **Persistence Context** | Hibernate's transaction-scoped cache |
| **Dirty Checking** | Automatic UPDATE detection on managed entities |
| **ddl-auto** | Schema management: none, validate, update, create |

---

## 🔧 Common Mistakes & Fixes

### Mistake 1: Wrong File Extension
```bash
# ❌ Spring Boot doesn't find this
application.yaml

# ✅ Use this
application.yml
```

### Mistake 2: Missing Driver
```xml
<!-- ✅ Must have this in pom.xml -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### Mistake 3: Docker Container Not Running
```bash
docker ps  # Check if PostgreSQL is running
docker start taskmanager-db  # If stopped
```

### Mistake 4: Wrong Port
```yaml
# Make sure port matches Docker mapping
url: jdbc:postgresql://localhost:5432/taskmanager_dev
```

---

## 📋 ddl-auto Options

| Value | What It Does | Safe for Production? |
|-------|--------------|---------------------|
| `none` | Does nothing | ✅ Yes |
| `validate` | Checks schema matches entities | ✅ Yes |
| `update` | Adds missing columns/tables | ❌ No |
| `create` | Drops and recreates all tables | ❌ No |
| `create-drop` | Creates, then drops on shutdown | ❌ No |

---

## 📊 Quick Reference

| Command | Purpose |
|---------|---------|
| `docker ps` | Show running containers |
| `docker start taskmanager-db` | Start PostgreSQL |
| `docker stop taskmanager-db` | Stop PostgreSQL |
| `docker logs taskmanager-db` | View PostgreSQL logs |
| `./mvnw clean` | Clean build |
| `./mvnw spring-boot:run` | Run application |

---

## ✅ Day 1 Checklist

### Setup
- [x] Docker installed and running
- [x] PostgreSQL container started
- [x] PostgreSQL accessible on port 5432

### Code
- [x] spring-boot-starter-data-jpa in pom.xml
- [x] PostgreSQL driver in pom.xml
- [x] application.yml with correct configuration
- [x] Main class with SpringBootApplication

### Understanding
- [x] Can explain the Object-Relational Impedance Mismatch
- [x] Know the difference between JDBC, JPA, Hibernate, Spring Data JPA
- [x] Understand why each layer exists
- [x] Can start/stop Docker PostgreSQL container

---
