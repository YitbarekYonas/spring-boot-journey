# Week 3, Day 4: One-to-Many & Many-to-One Relationships

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-4-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-OneToMany%20%26%20ManyToOne-orange.svg)]()

> **"One User can have many Tasks. Many Tasks belong to one User."**

---

## 🎯 Learning Objectives

- ✅ Understand the **Object-Relational Impedance Mismatch** for relationships
- ✅ Map **@ManyToOne** on the owning side (Task → User)
- ✅ Map **@OneToMany** on the inverse side (User → Tasks)
- ✅ Understand **owning side vs inverse side**
- ✅ Use **mappedBy** correctly
- ✅ Configure **cascade types** (PERSIST, MERGE, REMOVE)
- ✅ Understand **LAZY vs EAGER** fetching
- ✅ Handle **LazyInitializationException**
- ✅ Use **fetch joins** to load relationships in one query

---

## 💡 What I Learned Today

### 1. Database Relationship
```
users table (one)          tasks table (many)
+----+-------+             +----+------------------+------------------+
| id | name  |             | id | title            | assigned_user_id |
+----+-------+             +----+------------------+------------------+
| 1  | Alice |             | 1  | Build REST API   | 1                |
| 2  | Bob   |             | 2  | Write tests      | 1                |
+----+-------+             | 3  | Deploy           | 2                |
                           +----+------------------+------------------+
```

### 2. JPA Annotations

| Annotation | Where | Purpose |
|------------|-------|---------|
| `@ManyToOne` | Task (many side) | Holds the foreign key |
| `@OneToMany` | User (one side) | Collection of tasks |
| `@JoinColumn` | Task | Specifies the FK column name |
| `mappedBy` | User | Points to field on owning side |

### 3. Owning Side vs Inverse Side

```
Owning Side   = Task (@ManyToOne)
              = has @JoinColumn
              = controls the FK in database
              = NO mappedBy

Inverse Side  = User (@OneToMany)
              = has mappedBy = "assignedUser"
              = does NOT control the FK
              = NO @JoinColumn
```

### 4. Cascade Types

| Type | What It Does |
|------|--------------|
| `PERSIST` | Save child when parent is saved |
| `MERGE` | Update child when parent is updated |
| `REMOVE` | Delete child when parent is deleted |
| `ALL` | All of the above |

**Best Practice:** Use `{PERSIST, MERGE}` without `REMOVE` to avoid accidental deletions.

### 5. Fetch Types

```
LAZY  → Load on demand (default for @OneToMany)
      → Better performance
      → Can cause LazyInitializationException

EAGER → Load immediately (default for @ManyToOne)
      → Always override to LAZY
```

### 6. LazyInitializationException
Accessing a lazy field outside a transaction → exception!

**Fixes:**
1. Keep access inside `@Transactional`
2. Use `JOIN FETCH` to load in one query
3. Use DTO projections

---

## 💻 Code Examples

### Task (Owning Side)
```java
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  // ALWAYS LAZY
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;
}
```

### User (Inverse Side)
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(
        mappedBy = "assignedUser",           // Field in Task
        fetch = FetchType.LAZY,
        cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private List<Task> tasks = new ArrayList<>();

    // Helper method - keeps both sides in sync
    public void addTask(Task task) {
        tasks.add(task);
        task.setAssignedUser(this);
    }
}
```

### Fetch Join Query
```java
@Query("SELECT t FROM Task t JOIN FETCH t.assignedUser WHERE t.id = :id")
Optional<Task> findByIdWithUser(@Param("id") Long id);
```

### Repository Methods
```java
public interface TaskRepository extends JpaRepository<Task, Long> {
    // LAZY loading - works only inside @Transactional
    Optional<Task> findById(Long id);

    // Fetch join - loads both in one query
    @Query("SELECT t FROM Task t JOIN FETCH t.assignedUser WHERE t.id = :id")
    Optional<Task> findByIdWithUser(@Param("id") Long id);
}
```

---

## 📊 Generated SQL

### LAZY Loading (Two Queries)
```sql
-- Query 1: Load Task only
SELECT t.id, t.title, t.assigned_user_id FROM tasks t WHERE t.id = ?

-- Query 2: User loaded ONLY when accessed
SELECT u.id, u.name, u.email FROM users u WHERE u.id = ?
```

### Fetch Join (One Query)
```sql
-- Load Task AND User in one query
SELECT t.*, u.*
FROM tasks t
LEFT JOIN users u ON u.id = t.assigned_user_id
WHERE t.id = ?
```

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| Forgetting `mappedBy` | Add `mappedBy = "assignedUser"` on @OneToMany |
| Using `EAGER` on @ManyToOne | Always use `FetchType.LAZY` |
| CascadeType.ALL deleting children | Use `{PERSIST, MERGE}` only |
| Only updating one side of relationship | Use helper method to sync both sides |
| Accessing lazy fields outside transaction | Use @Transactional or fetch join |

---

## ✅ Checklist

- [x] Added `@ManyToOne` to Task with `assignedUser`
- [x] Added `@OneToMany` to User with `tasks`
- [x] Used `mappedBy` correctly on @OneToMany
- [x] Set `fetch = FetchType.LAZY` on both sides
- [x] Used cascade `{PERSIST, MERGE}` without REMOVE
- [x] Verified foreign key in database
- [x] Demonstrated LAZY loading inside transaction
- [x] Triggered LazyInitializationException
- [x] Used fetch join to load in one query

---
