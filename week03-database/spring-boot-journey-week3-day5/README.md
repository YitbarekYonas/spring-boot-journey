# Week 3, Day 5: Many-to-Many Relationships

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-5-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Many%20to%20Many-orange.svg)]()

> **"Neither table can hold the foreign key — use a join table."**

---

## 🎯 Learning Objectives

- ✅ Understand when to use Many-to-Many relationships
- ✅ Create join table with `@JoinTable`
- ✅ Map bidirectional Many-to-Many with `mappedBy`
- ✅ Use sync helpers to manage both sides
- ✅ Understand cascade rules for Many-to-Many
- ✅ Identify N+1 problem in console logs
- ✅ Fix N+1 with `JOIN FETCH`

---

## 💡 What I Learned Today

### 1. Many-to-Many Requires a Join Table
When a task has multiple tags and a tag belongs to multiple tasks, neither table can hold the foreign key. The solution is a **join table**:

```sql
task_tags:
| task_id | tag_id |
|---------|--------|
| 1       | 1      |  ← Task 1 has tag "backend"
| 1       | 2      |  ← Task 1 has tag "urgent"
```

### 2. Mapping Many-to-Many

**Owning Side (Task):**
```java
@ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
@JoinTable(
    name = "task_tags",
    joinColumns = @JoinColumn(name = "task_id"),
    inverseJoinColumns = @JoinColumn(name = "tag_id")
)
private List<Tag> tags = new ArrayList<>();
```

**Inverse Side (Tag):**
```java
@ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
private List<Task> tasks = new ArrayList<>();
```

### 3. Sync Helpers (Critical!)
Always use sync helpers to maintain both sides:

```java
public void addTag(Tag tag) {
    if (!tags.contains(tag)) {
        tags.add(tag);
        tag.addTaskInternal(this);  // Update inverse side
    }
}
```

### 4. Cascade Rules
- ✅ Use: `CascadeType.PERSIST`, `CascadeType.MERGE`
- ❌ Never use: `CascadeType.REMOVE` or `CascadeType.ALL`
- `REMOVE` would delete shared tags that other tasks depend on!

### 5. The N+1 Problem

**Problem:**
```java
List<Task> tasks = taskRepository.findAll();  // 1 query
for (Task task : tasks) {
    System.out.println(task.getTags().size()); // N queries!
}
```

**SQL Output:**
```sql
SELECT * FROM tasks                    -- Query 1
SELECT tags WHERE task_id = 1          -- Query 2
SELECT tags WHERE task_id = 2          -- Query 3
SELECT tags WHERE task_id = 3          -- Query 4
-- Total: 1 + N queries!
```

**Fix with JOIN FETCH:**
```java
@Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.tags")
List<Task> findAllWithTags();  // 1 query total!
```

**SQL Output:**
```sql
SELECT DISTINCT t.*, tag.*
FROM tasks t
LEFT JOIN task_tags tt ON tt.task_id = t.id
LEFT JOIN tags tag ON tag.id = tt.tag_id
-- Only 1 query!
```

### 6. Detecting N+1
Enable Hibernate statistics:
```yaml
spring:
  jpa:
    properties:
      hibernate:
        generate_statistics: true
```

Look for repeated SELECT statements with different IDs in the logs.

---

## 💻 Code Examples

### Task Entity (Owning Side)
```java
@ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
@JoinTable(
    name = "task_tags",
    joinColumns = @JoinColumn(name = "task_id"),
    inverseJoinColumns = @JoinColumn(name = "tag_id")
)
private List<Tag> tags = new ArrayList<>();

public void addTag(Tag tag) {
    if (!tags.contains(tag)) {
        tags.add(tag);
        tag.addTaskInternal(this);
    }
}
```

### Tag Entity (Inverse Side)
```java
@ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
private List<Task> tasks = new ArrayList<>();

void addTaskInternal(Task task) {
    if (!tasks.contains(task)) {
        tasks.add(task);
    }
}
```

### Repository with JOIN FETCH
```java
@Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.tags")
List<Task> findAllWithTags();

@Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.tags WHERE t.id = :id")
Optional<Task> findByIdWithTags(@Param("id") Long id);
```

### Seeding Data
```java
Tag backend = tagRepository.save(new Tag("backend", "Backend"));
Tag urgent = tagRepository.save(new Tag("urgent", "Urgent"));

Task task = new Task("Build API", ...);
task.addTag(backend);
task.addTag(urgent);
taskRepository.save(task);
```

---

## 📊 Generated SQL

### Join Table Creation
```sql
create table task_tags (
    task_id bigint not null,
    tag_id bigint not null,
    primary key (task_id, tag_id),
    foreign key (tag_id) references tags(id),
    foreign key (task_id) references tasks(id)
)
```

### N+1 Problem (Bad)
```sql
select * from tasks           -- 1 query
select * from task_tags where task_id = 1  -- Query 2
select * from task_tags where task_id = 2  -- Query 3
```

### JOIN FETCH Fix (Good)
```sql
select distinct t.*, tag.*
from tasks t
left join task_tags tt on tt.task_id = t.id
left join tags tag on tag.id = tt.tag_id
-- Only 1 query!
```

---

## 🔧 Testing Commands

```bash
# Test N+1 (bad)
curl http://localhost:8080/api/tasks/all

# Test JOIN FETCH (good)
curl http://localhost:8080/api/tasks/all-with-tags

# Verify join table
docker exec -it taskmanager-db psql -U devuser -d taskmanager_dev -c "SELECT * FROM task_tags;"
```

---

## 📊 Quick Reference

| Concept | My Understanding |
|---------|------------------|
| **Join Table** | Third table holding both FKs as composite PK |
| **@JoinTable** | Defines join table on owning side |
| **mappedBy** | References owning side field name (inverse side) |
| **Owning Side** | Where `@JoinTable` lives (Task → tags) |
| **Cascade** | Use PERSIST + MERGE only, never REMOVE |
| **N+1 Problem** | 1 query for parent + N queries for children |
| **JOIN FETCH** | Loads children in same query using LEFT JOIN |
| **Sync Helper** | Maintains both sides of relationship |

---

## ✅ Day 5 Checklist

### Concepts
- [x] Understand Many-to-Many requires a join table
- [x] Know when to use `@JoinTable` vs `mappedBy`
- [x] Understand cascade rules for Many-to-Many
- [x] Identify N+1 problem in logs
- [x] Fix N+1 with JOIN FETCH

### Code
- [x] Created Tag entity
- [x] Added `@ManyToMany` on Task with `@JoinTable`
- [x] Added `mappedBy` on Tag (inverse side)
- [x] Added sync helpers (`addTag`, `removeTag`)
- [x] Created TagRepository
- [x] Added `findAllWithTags()` with JOIN FETCH
- [x] Seeded tasks with tags

### Testing
- [x] Verified join table created in database
- [x] Demonstrated N+1 with `/api/tasks/all`
- [x] Fixed N+1 with `/api/tasks/all-with-tags`
- [x] Verified query count difference in logs

---
