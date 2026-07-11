# Week 3, Day 6: Hibernate Internals & Common Pitfalls

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-6-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Hibernate%20Internals-orange.svg)]()

> **"Understanding Hibernate internals turns mysterious bugs into predictable behavior."**

---

## 🎯 Learning Objectives

- ✅ Understand the **Entity Lifecycle** (Transient, Persistent, Detached, Removed)
- ✅ Understand **Dirty Checking** - updates happen without calling `save()`
- ✅ Understand **Persistence Context** (first-level cache)
- ✅ Understand **LazyInitializationException** and how to fix it
- ✅ Understand **`@Transactional`** behavior and rollback rules
- ✅ Understand `equals()`/`hashCode()` pitfalls with JPA entities
- ✅ Understand **Detached Entity** problem and how to fix it

---

## 💡 What I Learned Today

### 1. The Entity Lifecycle - Four States

| State | Description |
|-------|-------------|
| **Transient** | `new` object - Hibernate doesn't know about it |
| **Persistent/Managed** | In persistence context - Hibernate tracks changes |
| **Detached** | Was managed, session closed - Hibernate doesn't track |
| **Removed** | Marked for deletion - removed at commit |

### 2. Dirty Checking - The Surprise Feature

```java
@Transactional
public void updateTask(Long id) {
    Task task = taskRepository.findById(id).get();  // Loaded → PERSISTENT
    task.setStatus(TaskStatus.IN_PROGRESS);         // Modified
    // No save() called! But UPDATE SQL fires at commit
}
```

**Key Insight:** Hibernate takes a snapshot when loading. At commit, it compares current state vs snapshot and generates UPDATE for any changes.

### 3. LazyInitializationException

**Problem:** Accessing a lazy-loaded collection outside a transaction.

**Fixes (in order of preference):**
1. **JOIN FETCH** - Load everything in one query
   ```java
   @Query("SELECT t FROM Task t LEFT JOIN FETCH t.tags WHERE t.id = :id")
   Optional<Task> findByIdWithTags(@Param("id") Long id);
   ```
2. **DTO projection** - Load only what you need
3. **`@Transactional`** - Keep session open (not recommended for controllers)
4. **Open Session in View** - Avoid this!

### 4. @Transactional Rules

| Rule | Explanation |
|------|-------------|
| **Rollback** | Rolls back on RuntimeException by default |
| **Checked Exceptions** | Do NOT roll back by default (use `rollbackFor`) |
| **Self-invocation** | Calling from within same class bypasses proxy |
| **Private methods** | @Transactional is silently ignored |

### 5. Detached Entity Problem

**Problem:** Saving an entity with an existing ID without merging.

**Solution 1 - Remove cascade:**
```java
@ManyToMany(fetch = FetchType.LAZY)  // No cascade
private List<Tag> tags = new ArrayList<>();
```

**Solution 2 - Save tags first, reuse managed instances:**
```java
Tag savedTag = tagRepository.save(tag);  // Becomes managed
task.getTags().add(savedTag);            // Use managed instance
taskRepository.save(task);
```

### 6. equals()/hashCode() Rules

| Rule | Why |
|------|-----|
| **Never use all fields** | ID changes break hash-based collections |
| **Never use @Data on entities** | toString/equals causes StackOverflowError |
| **Use business key** | Natural key (email, username) that never changes |
| **Or use UUID** | Stable identifier before save |

**✅ Correct equals()/hashCode():**
```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Tag)) return false;
    Tag tag = (Tag) o;
    return Objects.equals(name, tag.name);  // Use stable business key
}

@Override
public int hashCode() {
    return Objects.hash(name);
}
```

### 7. Common Pitfalls

| Pitfall | Fix |
|---------|-----|
| `@Data` on entities | Use `@Getter`, `@Setter`, `@ToString(exclude=...)` |
| Missing `@Transactional` on write | Add `@Transactional` |
| `ddl-auto: create` in production | NEVER - use `none` or `validate` |
| Lazy access outside transaction | Use JOIN FETCH or DTO |
| Detached entity passed to persist | Remove cascade or use managed instances |

### 8. Persistence Context (First-Level Cache)

```java
@Transactional
public void demo() {
    Task task1 = repo.findById(1L).get();  // SQL executed
    Task task2 = repo.findById(1L).get();  // NO SQL - from cache
    System.out.println(task1 == task2);    // true - same object
}
```

---

## 📊 Quick Reference

| Concept | My Understanding |
|---------|------------------|
| **Persistence Context** | First-level cache - same ID returns same object |
| **Dirty Checking** | Hibernate auto-detects changes to managed entities |
| **Lazy Loading** | Loads data only when accessed |
| **N+1 Problem** | 1 query for parent + N queries for children |
| **JOIN FETCH** | Loads lazy associations in one query |
| **Detached Entity** | Entity with ID but not in persistence context |
| **merge()** | Re-attaches a detached entity |
| **persist()** | Makes a transient entity managed |

---

## ✅ Day 6 Checklist

### Concepts
- [x] Understand Entity Lifecycle (Transient, Persistent, Detached, Removed)
- [x] Understand Dirty Checking
- [x] Understand Persistence Context / First-level cache
- [x] Understand LazyInitializationException
- [x] Fix LazyInitializationException with JOIN FETCH
- [x] Understand @Transactional rollback rules
- [x] Understand Detached Entity problem
- [x] Understand equals()/hashCode() pitfalls
- [x] Understand self-invocation issue

### Code
- [x] Demonstrated Dirty Checking (UPDATE without save())
- [x] Demonstrated LazyInitializationException
- [x] Fixed LazyInitializationException with JOIN FETCH
- [x] Proper equals()/hashCode() implementation
- [x] Removed `@Data` from entities
- [x] Added `@ToString(exclude = "...")`
- [x] Fixed Detached Entity problem

---
