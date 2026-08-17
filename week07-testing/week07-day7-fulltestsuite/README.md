# Week 7, Day 7: Capstone — Full Test Suite for the Task Manager

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-7-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Full%20Testing%20Pyramid-orange.svg)]()

> **"Every layer proves a different thing. Together they prove the app works — cheaply, quickly, and with confidence that scales as the codebase grows."**

---

## 🎯 What This Project Is

The Week 7 mini-project, built to spec: one Task Manager app, tested at **every** layer the week covered.

| Layer | Technique | File | # Tests |
|---|---|---|---|
| 1 | Mockito unit tests (service, repository mocked) | `TaskServiceTest` | 12 |
| 2 | `@WebMvcTest` (controller, service mocked) | `TaskControllerTest` | 11 |
| 3 | `@DataJpaTest` (repository, embedded H2) | `TaskRepositoryTest` | 10 |
| 4 | Testcontainers E2E (nothing mocked, real Postgres) | `TaskManagerCriticalFlowIT` | 1 |

34 tests total, each earning its place at the layer where it's cheapest to write and fastest to run.

This project also adds one new thing beyond Days 3–6: a **real business rule** (`markAsDone()` — a task can only reach `DONE` by first passing through `IN_PROGRESS`). Pure CRUD doesn't give unit tests much to prove; a state-transition rule does.

---

## 💡 Why Four Layers, Not Just One

A natural question: if the Testcontainers E2E test proves the whole stack works, why bother with the other 33 tests?

**Because of what happens when something breaks.** With only the E2E layer:
- A failing test tells you "something in create→update→done is broken" — but not *what*, or *where*.
- Every test run needs Docker, a container boot, a full Spring context. Multiply that by dozens of edge cases and your suite takes minutes instead of seconds.
- You'd need a dozen slow, expensive E2E tests to cover the same edge cases 3 fast Mockito tests already cover for free.

**With all four layers:**
- A break in business logic → `TaskServiceTest` fails, pinpointing the exact rule, in under a second, no DB needed.
- A break in the HTTP contract (wrong status code, wrong JSON shape) → `TaskControllerTest` fails, no DB needed.
- A break in a query → `TaskRepositoryTest` fails, fast, against H2.
- The single `TaskManagerCriticalFlowIT` exists purely as a final sanity check that the *real* wiring — real HTTP, real DB engine — hasn't drifted from what the isolated layers assume.

This is the testing pyramid in practice: many fast, narrow tests at the bottom; one or a few slow, broad tests at the top.

---

## 🏗️ What's New in This Project (vs. Days 3–6)

### A real business rule
```java
public TaskResponse markAsDone(Long id) {
    Task task = findTaskOrThrow(id);

    if (task.getStatus() == Task.Status.DONE) {
        throw new InvalidTaskStateException("Task " + id + " is already DONE");
    }
    if (task.getStatus() == Task.Status.TODO) {
        throw new InvalidTaskStateException(
                "Task " + id + " must be IN_PROGRESS before it can be marked DONE");
    }

    task.setStatus(Task.Status.DONE);
    return TaskResponse.fromEntity(taskRepository.save(task));
}
```
This is tested at **three** layers, each proving something different:
- `TaskServiceTest` — proves the branching logic itself, with the repository fully mocked (fastest, most precise failure location)
- `TaskControllerTest` — proves a thrown `InvalidTaskStateException` correctly becomes an HTTP `409 Conflict` with the right body shape
- `TaskManagerCriticalFlowIT` — proves the *whole* rule survives a real HTTP round-trip against a real database, as part of the one critical-flow test

### `PATCH /api/tasks/{id}/done`
A new endpoint, deliberately using `PATCH` (partial update — the roadmap's Week 2 HTTP verb table) rather than `PUT` (full replace), since this operation changes exactly one field with server-side rules attached, not a client-supplied full representation.

### `InvalidTaskStateException` → `409 Conflict`
A second custom exception type in `GlobalExceptionHandler`, mapped to `409` (not `400` — this isn't malformed input, it's a request that's well-formed but conflicts with the resource's current state) or `404` (the resource exists, just isn't in a state that allows this action).

---

## 📋 Layer-by-Layer Test Breakdown

### 1. `TaskServiceTest` (Mockito)
Covers every service method, with the repository entirely mocked:
- `createTask`, `getTaskById` (found + not-found), `getAllTasks`, `getTasksByStatus`
- `updateTask` (success + not-found, with `verify(repo, never()).save(...)` on the failure path)
- `deleteTask` (success + not-found)
- `markAsDone` — all three branches (valid transition, rejected from `TODO`, rejected from `DONE`)
- Uses `ArgumentCaptor` once to verify **exactly** what gets passed to `save()`, not just that it was called

### 2. `TaskControllerTest` (`@WebMvcTest`)
Covers the full HTTP contract for every endpoint, service mocked via `@MockBean`:
- Happy paths for all 6 endpoints (`POST`, `GET` by id, `GET` list with/without `?status=`, `PUT`, `PATCH .../done`, `DELETE`)
- Validation failures (blank title, invalid email) → `400` with `fieldErrors`
- Not-found → `404` with the structured `ErrorResponse` shape
- Business-rule conflict → `409`

### 3. `TaskRepositoryTest` (`@DataJpaTest`)
Every derived query method + the custom JPQL query, against embedded H2, fixtures via `TestEntityManager` with `entityManager.clear()` to force real reads.

### 4. `TaskManagerCriticalFlowIT` (Testcontainers)
The **one** end-to-end test, deliberately scoped to the single most important journey: create → attempt invalid transition (rejected) → progress → complete (accepted) → verify final persisted state directly against the database. This is where the business rule, the HTTP layer, the exception handler, and real Postgres all have to agree simultaneously.

---

## ❌ Common Mistakes (Week 7 Retrospective)

| Mistake | Fix |
|---------|-----|
| Writing dozens of Testcontainers tests instead of one focused critical-flow test | Push edge cases down to the cheaper layers; keep the top of the pyramid small |
| Testing the business rule only at the E2E layer | Slowest possible feedback loop for logic that Mockito could catch in milliseconds |
| Mocking the service in "integration" tests | Defeats the purpose — you're just re-running `@WebMvcTest` with extra ceremony |
| Skipping the not-found / conflict paths at every layer | Happy-path-only coverage hides exactly the bugs that matter in production |
| Forgetting `@BeforeEach` cleanup in the Testcontainers test | `@SpringBootTest` isn't auto-rolled-back like `@DataJpaTest` — explicit `deleteAll()` keeps tests independent |
| Choosing `PUT` for a partial, rule-gated status change | `PATCH` communicates intent correctly and matches the roadmap's Week 2 HTTP verb guidance |

---

## ✅ Week 7 Capstone Checklist

- [x] Unit tests for all service methods (Mockito), including a real business rule
- [x] `@WebMvcTest` for the full controller — happy paths, validation, not-found, conflict
- [x] `@DataJpaTest` for all custom repository queries
- [x] One Testcontainers-backed integration test covering the critical user flow
- [x] Meaningful coverage on service + controller layers — behavior tested, not lines chased
- [x] Consistent error response shape across every failure mode, verified at multiple layers

---

## 🐳 Running This Locally

```bash
# Fast layers — no Docker needed
mvn test -Dtest=TaskServiceTest,TaskControllerTest,TaskRepositoryTest

# Full suite, including the Testcontainers test (requires Docker running)
docker info
mvn test
```

---

**Date**: August 15, 2026
**Status**: ✅ Week 7 Complete!
**Next**: Week 8 — Deployment: Docker, Docker Compose, CI/CD, Cloud Deployment. Time to wrap this Task Manager in a `Dockerfile`, wire up GitHub Actions to run exactly this test suite on every push, and ship it to a public URL.

> *"Fast tests catch bugs. A pyramid of them catches bugs AND tells you exactly where to look."*
