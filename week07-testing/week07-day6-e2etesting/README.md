# Week 7, Day 6: Full End-to-End Integration Tests

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-6-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-TestRestTemplate-orange.svg)]()

> **"Every previous test in this week proved one layer. This one proves the layers actually talk to each other."**

---

## 📝 A Scope Note Before You Read This

The roadmap's Day 6 exercise describes a JWT-secured register → login → create → fetch flow. That's Week 5–6 territory (auth, JWT, roles) that this testing-focused mini-series (Days 3–6) never built — every prior day has deliberately stayed scoped to the `Task` domain with no auth layer, so each day's *testing technique* stays the clear focus rather than re-deriving a security module.

So Day 6 here builds what's actually new to *this* day — a full `Controller → Service → Repository` stack (previous days only had a repository, or a controller with a mocked service) — and end-to-end tests it with real HTTP calls against a real Postgres container. That's the correct target for "prove the full stack really works," which is the actual point of Day 6. If/when you build the JWT module from Week 5–6, this exact test pattern (`TestRestTemplate` + `RANDOM_PORT` + Testcontainers) is what you'd reuse for the register → login → protected-route flow.

---

## 🎯 Learning Objectives

- ✅ Understand `@SpringBootTest(webEnvironment = RANDOM_PORT)` — a real embedded server, not a simulated one
- ✅ Use `TestRestTemplate` to make genuine HTTP calls against that server
- ✅ Prove the full stack: HTTP → `@RestController` → `@Service` → `JpaRepository` → real PostgreSQL (Testcontainers)
- ✅ Confirm `@Valid` + `GlobalExceptionHandler` are correctly wired into the real request pipeline, not just tested in isolation
- ✅ Use a test data builder to avoid duplicating fixture setup across tests
- ✅ Cross-check API responses against direct repository reads, to catch a lying HTTP layer

---

## 💡 What I Learned Today

### 1. Where This Sits in the Testing Pyramid

| Day | Technique | What's Real | What's Mocked/Skipped | Speed |
|---|---|---|---|---|
| 2 | Mockito unit tests | Service logic | Repository | Fastest |
| 3 | `@WebMvcTest` | HTTP contract, controller | Service (`@MockBean`) | Fast |
| 4 | `@DataJpaTest` | Repository queries | H2, not real DB | Fast |
| 5 | Testcontainers (repo-only) | Repository queries, real Postgres | Controller/Service (not loaded) | Slower |
| **6** | **Full E2E** | **Everything — HTTP, controller, service, repository, real Postgres** | **Nothing** | **Slowest** |

Nothing is mocked in this file. That's the entire point — and also why it's the slowest tier and the smallest in count (Day 6 has 6 focused tests, not 15+).

### 2. `RANDOM_PORT` vs `MockMvc`

`@WebMvcTest` (Day 3) uses `MockMvc`, which **simulates** an HTTP request inside the same JVM — no real socket, no real serialization over the wire.

`@SpringBootTest(webEnvironment = RANDOM_PORT)` starts an **actual embedded server** (Tomcat by default) bound to a real, available port, injected via `@LocalServerPort`. `TestRestTemplate` then sends **real HTTP requests** over a real socket. This is the only way to be sure things like content negotiation, real JSON serialization, and the full filter chain genuinely work — not just "work in a simulation of them."

```java
@LocalServerPort
private int port;

@Autowired
private TestRestTemplate restTemplate;

private String baseUrl() {
    return "http://localhost:" + port + "/api/tasks";
}
```

### 3. Why Testcontainers Again (Combined With RANDOM_PORT This Time)

Day 5 combined Testcontainers with `@SpringBootTest` for repository-focused integration tests. Day 6 combines the same Testcontainers setup with `RANDOM_PORT` + `TestRestTemplate` — now the *entire* app, not just the repository, is under real end-to-end test, against the real database engine.

### 4. Full Flows, Not Isolated Assertions

Each of the three "flow" tests chains multiple real HTTP calls together, mirroring how a client would actually use the API:

```java
// FLOW 1
POST /api/tasks         → 201 Created, Location header, body has generated id
GET  /api/tasks/{id}    → 200 OK, same data comes back
taskRepository.findById → cross-check directly against the DB
```

That last line matters: asserting only on the HTTP response could theoretically pass even if persistence were broken (if, say, the response DTO were built from the in-memory request object instead of the saved entity). Reading straight from the repository closes that gap.

### 5. Proving the Exception Handler Is Really Wired In

Day 6's edge-case tests (`getNonExistentTask_returns404...`, `createTaskWithBlankTitle_returns400...`) look similar to what a `@WebMvcTest` might check — but the difference is everything here is real: a real `ResourceNotFoundException` thrown from a real service, intercepted by a real `@RestControllerAdvice`, serialized by real Jackson, sent over a real socket, and deserialized back into `ErrorResponse` by `TestRestTemplate`. If any link in that chain were broken, these tests would fail — a `@WebMvcTest` covering the same case would not catch a break outside the web layer.

### 6. Test Data Builders

```java
private TaskRequest validTaskRequest() {
    return TaskRequest.builder()
            .title("Fix login bug")
            .status(Task.Status.TODO)
            .priority(Task.Priority.HIGH)
            .ownerEmail("alice@example.com")
            .build();
}
```

Every test that needs a valid payload starts here and overrides only the field it cares about (e.g. blanking `title` for the validation test). Keeps each test's *intent* readable — you see immediately what's different about this particular case instead of re-reading a full object literal each time.

---

## 📋 Test Summary

| Test | Concept Demonstrated |
|------|---------------------|
| `createThenFetchTask_worksEndToEnd` | Full create→fetch flow, cross-checked against the repository directly |
| `createThenUpdateTask_persistsChanges` | Full create→update→verify flow |
| `createFilterAndDeleteTasks_worksEndToEnd` | Multi-entity flow: create, filter by query param, delete, confirm removal |
| `getNonExistentTask_returns404WithErrorBody` | `GlobalExceptionHandler` wired into the real pipeline |
| `createTaskWithBlankTitle_returns400WithFieldErrors` | `@Valid` + field-level error messages, end-to-end |
| `createTaskWithInvalidEmail_returns400` | `@Email` constraint enforced through the real HTTP layer |

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| Mocking the service layer "to make E2E tests faster" | Defeats the entire purpose of an E2E test — you're back to testing `@WebMvcTest`'s job |
| Asserting only on the HTTP response, never checking the DB directly | A subtly broken persistence layer can still return a plausible-looking response |
| Hardcoding `localhost:8080` instead of using `@LocalServerPort` | Random port avoids collisions; hardcoding breaks parallel test runs and CI |
| Writing one giant test that does everything | Keep each flow test focused on one user journey; keep edge cases in separate, smaller tests |
| Skipping cleanup between tests | `@BeforeEach` calling `taskRepository.deleteAll()` keeps tests independent — order should never matter |
| Only testing happy paths at this tier | The two validation/not-found tests exist specifically to prove error handling survives the full round-trip, not just the service layer |

---

## ✅ Day 6 Checklist

- [x] `@SpringBootTest(webEnvironment = RANDOM_PORT)`
- [x] `@LocalServerPort` + `TestRestTemplate`
- [x] Testcontainers `PostgreSQLContainer` + `@DynamicPropertySource`
- [x] Full `Controller → Service → Repository` stack (new this week — Days 3–5 only had partial slices)
- [x] At least one full multi-step flow (create → fetch, create → update → verify)
- [x] At least one test cross-checking the HTTP response against a direct repository read
- [x] At least one test proving error handling survives the full HTTP round-trip
- [x] Test data builder to avoid fixture duplication
- [x] `@BeforeEach` cleanup for test independence

---
