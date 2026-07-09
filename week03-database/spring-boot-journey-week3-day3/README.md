# Week 3, Day 3: Spring Data JPA Repositories

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-3-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-JPA%20Repositories-orange.svg)]()

> **"A repository is an interface you declare, and Spring implements at runtime."**

---

## 🎯 Learning Objectives

- ✅ Extend `JpaRepository<T, ID>` for CRUD operations
- ✅ Use **derived query methods** with naming conventions
- ✅ Write custom queries with `@Query` and JPQL
- ✅ Perform bulk updates/deletes with `@Modifying`
- ✅ Understand `save()` INSERT vs UPDATE logic
- ✅ Seed data with `@PostConstruct` and `DataSeeder`
- ✅ Test repository methods via debug controller

---

## 💡 What I Learned Today

### 1. JpaRepository - What You Get For Free

```java
public interface TaskRepository extends JpaRepository<Task, Long> {
    // 18 methods inherited:
    // save(), findById(), findAll(), delete(), count(), existsById()
}
```

### 2. Derived Query Methods

| Prefix | Purpose | Generated SQL |
|--------|---------|---------------|
| `findByStatus` | Find by single field | `WHERE status = ?` |
| `findByStatusAndPriority` | Multiple conditions | `WHERE status = ? AND priority = ?` |
| `findByTitleContainingIgnoreCase` | Text search | `WHERE LOWER(title) LIKE LOWER('%?%')` |
| `findByDueDateBefore` | Date comparison | `WHERE due_date < ?` |
| `countByStatus` | Count records | `SELECT COUNT(*) WHERE status = ?` |
| `findTop5ByOrderByCreatedAtDesc` | Limit + sort | `ORDER BY created_at DESC LIMIT 5` |

### 3. Custom Queries with @Query

```java
@Query("SELECT t FROM Task t WHERE t.dueDate < :today AND t.status NOT IN (:excludedStatuses)")
List<Task> findOverdueTasks(@Param("today") LocalDate today, @Param("excludedStatuses") List<TaskStatus> statuses);
```

### 4. Modifying Queries

```java
@Modifying
@Transactional
@Query("UPDATE Task t SET t.status = :newStatus WHERE t.status = :oldStatus")
int updateStatusBulk(@Param("oldStatus") TaskStatus oldStatus, @Param("newStatus") TaskStatus newStatus);
```

### 5. save() Logic

```java
// id == null → INSERT (new entity)
Task newTask = new Task(...);
taskRepository.save(newTask);

// id != null → UPDATE (existing entity)
Task existing = taskRepository.findById(1L).get();
existing.setTitle("Updated");
taskRepository.save(existing);
```

### 6. Data Seeder

```java
@Component
public class DataSeeder {
    @PostConstruct
    @Transactional
    public void seed() {
        if (taskRepository.count() == 0) {
            taskRepository.saveAll(List.of(...));
        }
    }
}
```

---

## 💻 Code Examples

### TaskRepository.java
```java
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // Derived queries
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByStatusAndPriority(TaskStatus status, TaskPriority priority);
    List<Task> findByTitleContainingIgnoreCase(String keyword);
    List<Task> findByDueDateBefore(LocalDate date);
    long countByStatus(TaskStatus status);
    List<Task> findTop5ByOrderByCreatedAtDesc();

    // Custom JPQL
    @Query("SELECT t FROM Task t WHERE t.dueDate < :today AND t.status NOT IN (:excludedStatuses)")
    List<Task> findOverdueTasks(@Param("today") LocalDate today, @Param("excludedStatuses") List<TaskStatus> statuses);

    // Bulk update
    @Modifying
    @Transactional
    @Query("UPDATE Task t SET t.status = :newStatus WHERE t.status = :oldStatus")
    int updateStatusBulk(@Param("oldStatus") TaskStatus oldStatus, @Param("newStatus") TaskStatus newStatus);
}
```

### TaskDebugController.java
```java
@RestController
@RequestMapping("/api/debug/tasks")
public class TaskDebugController {
    private final TaskRepository taskRepository;

    @GetMapping("/all")
    public List<Task> all() { return taskRepository.findAll(); }

    @GetMapping("/by-status/{status}")
    public List<Task> byStatus(@PathVariable TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    @GetMapping("/search")
    public List<Task> search(@RequestParam String keyword) {
        return taskRepository.findByTitleContainingIgnoreCase(keyword);
    }
}
```

### application.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/taskmanager_dev
    username: devuser
    password: devpass
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

---

## 📊 Generated SQL Examples

```sql
-- findByStatus(TODO)
select ... from tasks where status=?

-- findByStatusAndPriority(TODO, HIGH)
select ... from tasks where status=? and priority=?

-- findByTitleContainingIgnoreCase("JWT")
select ... from tasks where lower(title) like lower('%'||?||'%')

-- countByStatus(TODO)
select count(*) from tasks where status=?

-- findOverdueTasks()
select ... from tasks where due_date<? and status not in (?,?)
order by priority desc, due_date
```

---

## 📋 Endpoints

| Endpoint | URL |
|----------|-----|
| All tasks | `GET /api/debug/tasks/all` |
| By status | `GET /api/debug/tasks/by-status/{status}` |
| By priority | `GET /api/debug/tasks/by-priority/{priority}` |
| Search | `GET /api/debug/tasks/search?keyword=` |
| Overdue | `GET /api/debug/tasks/overdue` |
| Count | `GET /api/debug/tasks/count-by-status/{status}` |
| Top 5 | `GET /api/debug/tasks/top5-recent` |

---

## ✅ Day 3 Checklist

### Setup
- [x] PostgreSQL running on port 5432
- [x] Database `taskmanager_dev` exists
- [x] application.yml configured correctly

### Repository
- [x] Extends JpaRepository<Task, Long>
- [x] Derived query methods
- [x] Custom @Query for overdue tasks
- [x] @Modifying for bulk updates

### Testing
- [x] DataSeeder seeds initial data
- [x] TaskDebugController endpoints working
- [x] SQL logs verified in console
- [x] All endpoints tested in Postman

---
