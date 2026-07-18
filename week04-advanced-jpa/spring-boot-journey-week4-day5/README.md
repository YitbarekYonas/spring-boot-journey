# Week 4, Day 5: Transactions Deep Dive

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-5-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Transactions-orange.svg)]()

> **"Transactions guarantee that multiple operations succeed together or fail together."**

---

## 🎯 Learning Objectives

- ✅ Understand ACID properties
- ✅ Use `@Transactional` for atomic operations
- ✅ Understand Propagation types (REQUIRED, REQUIRES_NEW, NESTED)
- ✅ Understand rollback rules (RuntimeException vs checked exceptions)
- ✅ Prove atomicity by demonstrating rollback
- ✅ Compare with and without transactions

---

## 💡 What I Learned Today

### 1. ACID Properties

| Property | Meaning |
|----------|---------|
| **Atomicity** | All operations succeed or none do |
| **Consistency** | Database stays valid before and after |
| **Isolation** | Concurrent transactions don't interfere |
| **Durability** | Committed data survives crashes |

### 2. `@Transactional` Mechanics

```java
@Transactional
public void transferBook() {
    // BEGIN TRANSACTION
    branchA.decrement();  // SQL queued
    branchB.increment();  // SQL queued
    throw new RuntimeException(); // ROLLBACK
    // Both changes undone - atomic!
}
```

### 3. Propagation Types

| Type | Behavior |
|------|----------|
| `REQUIRED` (default) | Join existing or create new |
| `REQUIRES_NEW` | Always create new (suspends existing) |
| `NESTED` | Create savepoint within existing |
| `SUPPORTS` | Join if exists |
| `MANDATORY` | Require existing |
| `NOT_SUPPORTED` | Run without transaction |
| `NEVER` | Run without transaction (throw if exists) |

### 4. Rollback Rules

- ✅ Rolls back on `RuntimeException` (unchecked)
- ❌ Does NOT roll back on checked `Exception`
- Fix: `@Transactional(rollbackFor = Exception.class)`

### 5. Transactions Prove Atomicity

**With Transaction:**
```
Before: Branch A=5, Branch B=3
1. Decrement A → 4
2. Increment B → 4
3. 💥 FAIL
After: Branch A=5, Branch B=3 ✅ (Atomic!)
```

**Without Transaction:**
```
Before: Branch A=5, Branch B=3
1. Decrement A → 4 (COMMITTED!)
2. Increment B → 4 (COMMITTED!)
3. 💥 FAIL
After: Branch A=4, Branch B=4 ❌ (Partial Commit!)
```

---

## 💻 Code Examples

### With Transaction (Atomic)

```java
@Transactional
public String transferBookWithTransaction(Long from, Long to, int copies) {
    BookBranch fromBranch = findBranch(from);
    fromBranch.setCopies(fromBranch.getCopies() - copies);
    bookBranchRepository.save(fromBranch);

    BookBranch toBranch = findBranch(to);
    toBranch.setCopies(toBranch.getCopies() + copies);
    bookBranchRepository.save(toBranch);

    throw new RuntimeException("Simulated failure!");
    // ✅ Both changes rolled back!
}
```

### Without Transaction (Partial)

```java
public String transferBookWithoutTransaction(Long from, Long to, int copies) {
    BookBranch fromBranch = findBranch(from);
    fromBranch.setCopies(fromBranch.getCopies() - copies);
    bookBranchRepository.save(fromBranch); // COMMITTED!

    BookBranch toBranch = findBranch(to);
    toBranch.setCopies(toBranch.getCopies() + copies);
    bookBranchRepository.save(toBranch); // COMMITTED!

    throw new RuntimeException("Simulated failure!");
    // ❌ First change is permanent!
}
```

### Rollback Options

```java
// Default - rolls back on RuntimeException only
@Transactional
public void method1() { }

// Rollback on ALL exceptions
@Transactional(rollbackFor = Exception.class)
public void method2() throws Exception { }

// No rollback for specific exceptions
@Transactional(noRollbackFor = IllegalArgumentException.class)
public void method3() { }
```

---

## 📋 Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/transfer/branches` | GET | View branch copies |
| `/transfer/with-transaction` | POST | Atomic transfer (rollback) |
| `/transfer/without-transaction` | POST | Partial transfer (commit) |

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| Catching exception inside @Transactional | Let it propagate |
| @Transactional on private method | Make it public |
| Self-invocation | Use separate class |
| Long transactions | Keep short, no network calls |
| Missing rollbackFor for checked exceptions | Add rollbackFor |

---

## ✅ Day 5 Checklist

### Concepts
- [x] ACID properties
- [x] @Transactional mechanics
- [x] Propagation types
- [x] Rollback rules
- [x] Transaction boundaries

### Code
- [x] BookBranch entity
- [x] Transfer with @Transactional
- [x] Transfer without @Transactional
- [x] Deliberate failure in both
- [x] DataSeeder

### Testing
- [x] With transaction = rollback verified
- [x] Without transaction = partial commit verified
- [x] Database state after each test checked
