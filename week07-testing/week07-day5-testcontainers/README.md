# Week 7, Day 5: Integration Testing with Testcontainers

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-5-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Testcontainers-orange.svg)]()

> **"H2 proves your query is syntactically valid JPQL. Testcontainers proves it actually works on the database you're going to ship."**

---

## 🎯 Learning Objectives

- ✅ Understand why H2 (Day 4) isn't enough for real confidence
- ✅ Spin up a real, disposable PostgreSQL container for tests with Testcontainers
- ✅ Wire the container's dynamic connection details into the Spring context with `@DynamicPropertySource`
- ✅ Understand the `@Testcontainers` + `@Container` lifecycle
- ✅ See a concrete case (case-insensitive `LIKE`) where Postgres and H2 genuinely behave differently
- ✅ Confirm real database-level constraints (`NOT NULL`) are enforced, not just Java-level validation

---

## 💡 What I Learned Today

### 1. Why H2 Isn't Enough

Day 4's `@DataJpaTest` ran everything against H2 — fast, zero setup, in-memory. But H2 is not Postgres. It emulates SQL dialects reasonably well, but "reasonably well" is not "identically." Concrete gaps:

| Behavior | H2 | Real PostgreSQL |
|---|---|---|
| `LIKE` case sensitivity | Case-insensitive by default in many modes | Case-**sensitive** by default (`ILIKE` or `LOWER()` needed) |
| Enum storage/comparison | Lenient | Strict typing, more edge cases around casing |
| Sequence/identity generation | Simplified emulation | Real `SERIAL`/`IDENTITY` behavior |
| Constraint enforcement | Present but sometimes more forgiving | Strict, matches production exactly |

A green `@DataJpaTest` suite gives you confidence in the *query logic*. It does not guarantee the query behaves identically on the database you actually deploy to. Testcontainers closes that gap.

### 2. The Container Lifecycle

```java
@Testcontainers
class TaskRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("taskdb_test")
            .withUsername("test")
            .withPassword("test");
}
```

- `@Testcontainers` (JUnit 5 extension) manages container start/stop for the class.
- `static` + `@Container` → **one container, shared across all test methods** in this class (started once, not per-test — much faster than starting Postgres fresh for every `@Test`).
- The image tag is **pinned** (`postgres:16-alpine`), not `latest` — a test suite that silently changes behavior because Docker Hub's `latest` moved out from under you is its own kind of flaky.

### 3. `@DynamicPropertySource` — the piece that actually wires it together

The container binds to a **random host port** chosen at startup, so we can't hardcode `spring.datasource.url` in `application.yml` ahead of time. `@DynamicPropertySource` runs after the container is up but before the Spring context initializes, and injects the real, resolved connection details:

```java
@DynamicPropertySource
static void configureDatasource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
}
```

This **overrides** whatever's in `application.yml` for the duration of this test class only — the app's real runtime config (pointing at a real deployed Postgres instance) is untouched.

### 4. `@SpringBootTest`, not `@DataJpaTest`, for genuine integration tests

Day 4 deliberately used the narrow `@DataJpaTest` slice. Day 5 uses the full `@SpringBootTest` context, because the entire point of an integration test is confidence in the **real wiring** — not an isolated slice. The trade-off is speed: this suite is slower (container startup + full context) but buys real end-to-end confidence.

### 5. A Concrete Dialect Difference, Proven

`findByTitleContainingIgnoreCase("ssl")` matching a task titled `"Renew SSL certificate"` is exactly the kind of assertion that's *trivially* true on H2 but not guaranteed on Postgres, where plain `LIKE` is case-sensitive. Passing this test against a real Postgres container proves Hibernate is correctly compensating (via `LOWER()` or equivalent) — not just that the test happens to pass on a lenient in-memory engine.

### 6. Database-Level Constraints Are Enforced For Real

```java
Task invalid = Task.builder().title(null)...build();

assertThrows(DataIntegrityViolationException.class,
        () -> taskRepository.saveAndFlush(invalid));
```

`@Column(nullable = false)` isn't just documentation here — against a real Postgres `NOT NULL` column, Hibernate's `saveAndFlush()` actually round-trips to the database and the database itself rejects the row.

---

## 📋 Test Summary

| Test | Concept Demonstrated |
|------|---------------------|
| `container_isRunningAndReachable` | Testcontainers lifecycle sanity check |
| `saveAndFindById_roundTripsAgainstRealPostgres` | Full save/read cycle against real Postgres |
| `findByStatusAndPriority_worksAgainstRealPostgres` | Derived query re-verified on the real engine |
| `findByTitleContainingIgnoreCase_isTrulyCaseInsensitiveOnPostgres` | Concrete H2-vs-Postgres dialect gap, closed |
| `findActiveTasksForOwner_ordersCorrectlyOnRealPostgres` | Custom JPQL ordering re-verified on the real engine |
| `nullTitle_violatesNotNullConstraint_onRealPostgres` | Real DB-level constraint enforcement |

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| Using `latest` as the container image tag | Pin a version (`postgres:16-alpine`) for reproducible tests |
| Starting a new container per `@Test` method | Use a `static` field so the container is shared across the class — massive speedup |
| Hardcoding `spring.datasource.url` for the container's port | Use `@DynamicPropertySource` — the port is randomized at startup |
| Mocking everything "to make integration tests fast" | Defeats the purpose — you're no longer testing real wiring, just re-testing your mocks |
| Forgetting Docker isn't running in CI | Most CI providers (GitHub Actions, GitLab CI) have Docker available by default — verify, don't assume |
| Treating Testcontainers tests as a replacement for `@DataJpaTest` | They're complementary — `@DataJpaTest` for fast iteration, Testcontainers for pre-merge confidence |

---

## ✅ Day 5 Checklist

- [x] `PostgreSQLContainer` declared as `static` + `@Container`
- [x] `@Testcontainers` on the test class
- [x] `@DynamicPropertySource` wiring the container's real JDBC URL/credentials
- [x] `@SpringBootTest` (full context, not a slice)
- [x] Explicit cleanup (`deleteAll()`) since there's no automatic rollback like `@DataJpaTest`
- [x] At least one test proving a genuine Postgres-vs-H2 behavioral difference
- [x] At least one test proving a real database-level constraint

---

## 🐳 Running This Locally

```bash
# Docker must be running locally (Docker Desktop, Colima, etc.)
docker info

mvn test
```

Testcontainers will pull `postgres:16-alpine` on first run (cached after that), start a container, run the suite against it, then tear the container down automatically — no manual cleanup needed.

---
