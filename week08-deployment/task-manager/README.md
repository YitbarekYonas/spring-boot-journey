# Task Manager API

[![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-2088FF.svg)]()
[![Java](https://img.shields.io/badge/Java-21-orange.svg)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-6DB33F.svg)]()
[![Docker](https://img.shields.io/badge/Docker-multi--stage-2496ED.svg)]()
[![Tests](https://img.shields.io/badge/tests-34%20passing-brightgreen.svg)]()
[![License](https://img.shields.io/badge/license-MIT-blue.svg)]()

A production-shaped Task Manager REST API, built from the ground up: layered architecture, JWT-ready structure, full test pyramid, containerized, and CI/CD-automated. Built as the capstone of an 8-week self-directed Spring Boot backend roadmap — this README is both the setup guide and the honest summary of what's actually implemented.

---

## 📖 Table of Contents

- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [API Reference](#-api-reference)
- [Getting Started](#-getting-started)
- [Testing](#-testing)
- [CI/CD Pipeline](#-cicd-pipeline)
- [Deployment](#-deployment)
- [Project Structure](#-project-structure)
- [What's Deliberately Not Included](#-whats-deliberately-not-included)
- [Roadmap Retrospective](#-roadmap-retrospective)

---

## 🏗 Architecture

```
                              ┌─────────────────────┐
                              │   Client (Postman,   │
                              │  curl, frontend app) │
                              └──────────┬───────────┘
                                         │ HTTP / JSON
                                         ▼
┌────────────────────────────────────────────────────────────────────┐
│                          TaskController                              │
│   Thin — HTTP concerns only: status codes, path/query params,       │
│   delegates everything else to the service layer.                    │
│   @Valid on every write endpoint (POST / PUT)                        │
└──────────────────────────────┬────────────────────────────────────┘
                                │
                                ▼
┌────────────────────────────────────────────────────────────────────┐
│                           TaskService                                │
│   All business logic lives here, including the one real state       │
│   machine rule: a Task can only reach DONE via IN_PROGRESS.         │
│   Constructor injection (@RequiredArgsConstructor) throughout.       │
└──────────────────────────────┬────────────────────────────────────┘
                                │
                                ▼
┌────────────────────────────────────────────────────────────────────┐
│                         TaskRepository                               │
│         extends JpaRepository<Task, Long>                            │
│   Derived queries + one custom JPQL query (findActiveTasksForOwner)  │
└──────────────────────────────┬────────────────────────────────────┘
                                │
                                ▼
                    ┌────────────────────────┐
                    │   PostgreSQL (real DB)  │
                    │   — Postgres 16, via    │
                    │  Docker Compose or a    │
                    │  managed cloud instance │
                    └────────────────────────┘

     ── Cross-cutting ──
     GlobalExceptionHandler  → consistent {timestamp, status, message,
                                path, fieldErrors} shape for every error
     Actuator (/actuator/health, /info) → container + platform health checks
     DTOs (TaskRequest / TaskResponse)  → entities never leave the service layer
```

**Request lifecycle, end to end:** `HTTP request → TaskController (@Valid) → TaskService (business rules) → TaskRepository (JPA) → PostgreSQL`, with `GlobalExceptionHandler` intercepting any exception thrown at any layer and returning a consistent error shape.

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language / Runtime | Java 21 |
| Framework | Spring Boot 3.4 (Web, Data JPA, Validation, Actuator) |
| Database | PostgreSQL 16 (H2 for isolated repository tests only) |
| Build | Maven |
| Testing | JUnit 5, Mockito, AssertJ, MockMvc, Testcontainers |
| Containerization | Docker (multi-stage build), Docker Compose |
| CI/CD | GitHub Actions → GitHub Container Registry |
| Deployment targets | Render (Blueprint) / Railway — config included for both |

---

## 📡 API Reference

Base URL: `http://localhost:8080/api/tasks` (local) — see [Deployment](#-deployment) for the live URL once deployed.

| Method | Endpoint | Description | Success | Failure Modes |
|---|---|---|---|---|
| `POST` | `/api/tasks` | Create a task | `201 Created` + `Location` header | `400` validation failure |
| `GET` | `/api/tasks/{id}` | Fetch one task | `200 OK` | `404` not found |
| `GET` | `/api/tasks` | List all tasks | `200 OK` | — |
| `GET` | `/api/tasks?status=TODO` | Filter by status | `200 OK` | — |
| `PUT` | `/api/tasks/{id}` | Full update | `200 OK` | `400` validation, `404` not found |
| `PATCH` | `/api/tasks/{id}/done` | Mark done (TODO→IN_PROGRESS→DONE only) | `200 OK` | `404` not found, `409` invalid transition |
| `DELETE` | `/api/tasks/{id}` | Delete a task | `204 No Content` | `404` not found |
| `GET` | `/actuator/health` | Liveness/readiness check | `200 OK` `{"status":"UP"}` | — |

### Request Body (`POST` / `PUT`)

```json
{
  "title": "Fix login bug",
  "description": "JWT token not refreshing correctly",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2026-08-20",
  "ownerEmail": "alice@example.com"
}
```

### Error Response Shape (every failure mode, every endpoint)

```json
{
  "timestamp": "2026-08-16T10:15:30",
  "status": 404,
  "message": "Task not found with id: 999",
  "path": "/api/tasks/999",
  "fieldErrors": null
}
```

### The One Real Business Rule

A task can only be marked `DONE` after passing through `IN_PROGRESS` — you can't jump straight from `TODO`, and you can't mark an already-`DONE` task done again. Attempting either returns `409 Conflict`. This rule is enforced in `TaskService` and tested at three separate layers (unit, `@WebMvcTest`, and the Testcontainers end-to-end flow) — see [Testing](#-testing).

---

## 🚀 Getting Started

### Prerequisites
- Java 21
- Maven (or use your IDE's bundled Maven)
- Docker + Docker Compose (for the containerized path)

### Option 1 — Run Locally with Docker Compose (recommended)

```bash
git clone <this-repo-url>
cd task-manager

cp .env.example .env

docker compose up --build

# In another terminal
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Try the API","status":"TODO","priority":"MEDIUM","ownerEmail":"you@example.com"}'

curl http://localhost:8080/api/tasks
curl http://localhost:8080/actuator/health
```

This starts both the app **and** a real Postgres container, networked together, with a persistent volume — the full Day 2/3 setup.

### Option 2 — Run Locally without Docker

Requires a Postgres instance running locally on `5432` (or override `DB_URL` in `application-dev.yml`).

```bash
./mvnw spring-boot:run
# or, if you don't have the Maven wrapper committed:
mvn spring-boot:run
```

Defaults to the `dev` profile — verbose SQL logging, `ddl-auto: update`, permissive.

### Option 3 — Build and Run the Docker Image Directly

```bash
docker build -t task-manager .
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=<your-postgres-host> \
  -e DB_PORT=5432 \
  -e DB_NAME=taskdb \
  -e DB_USERNAME=<user> \
  -e DB_PASSWORD=<password> \
  task-manager
```

---

## 🧪 Testing

34 tests across the full testing pyramid — each layer earning its place at the speed where it's cheapest to catch a bug:

| Layer | File | What it proves | Speed |
|---|---|---|---|
| Unit (Mockito) | `TaskServiceTest` | Business logic, repository fully mocked — including all 3 branches of the `markAsDone` state rule | Fastest |
| Web (`@WebMvcTest`) | `TaskControllerTest` | HTTP contract — status codes, JSON shape, validation, service mocked | Fast |
| Repository (`@DataJpaTest`) | `TaskRepositoryTest` | Every derived query + the custom JPQL query, against embedded H2 | Fast |
| End-to-end (Testcontainers) | `TaskManagerCriticalFlowIT` | The one critical user journey — create → reject invalid transition → progress → complete — against a **real** Postgres container, nothing mocked | Slowest (1 test, deliberately) |

```bash
# Fast layers only (no Docker needed)
mvn test -Dtest=TaskServiceTest,TaskControllerTest,TaskRepositoryTest

# Full suite, including the Testcontainers test (Docker required)
mvn test
```

---

## ⚙️ CI/CD Pipeline

Every push to `main` and every pull request triggers `.github/workflows/ci-cd.yml`:

```
┌─────────────────────┐        ┌──────────────────────────┐
│   build-and-test     │──────▶│  build-and-push-image     │
│                       │ needs │  (main branch pushes      │
│  checkout → JDK 21    │       │   only, after tests pass) │
│  → compile → test     │       │                            │
│  (all 34 tests, incl.  │       │  build multi-stage image  │
│   Testcontainers)      │       │  → tag :latest + :<sha>   │
│  → publish results     │       │  → push to ghcr.io        │
│  → upload jar artifact │       └──────────────────────────┘
└─────────────────────┘
```

A Docker image only ever gets built and pushed if every test passes first — the `needs:` dependency between the two jobs makes that a guarantee, not a convention. See `.github/workflows/ci-cd.yml` for the full definition, and Week 8 Days 4–5's notes for the reasoning behind each choice (dependency caching, why compile/test are separate steps, tagging strategy, registry auth).

---

## ☁️ Deployment

Deployment configs for two platforms are included and ready to use:

- **`render.yaml`** — a Render Blueprint: app + managed Postgres, health-check-gated rollout, database credentials wired automatically via `fromDatabase` references (never committed as literal values)
- **`railway.json`** — Dockerfile-based build config for Railway, paired with Railway's dashboard-based reference variables for the Postgres plugin

Full step-by-step instructions for both platforms, plus a conceptual AWS EC2/Elastic Beanstalk/ECS overview, live in **Week 8 Day 6's notes** in this repo's history. *(Live URL: add here once deployed — see the honesty note in Day 6's README about why that step is yours to complete.)*

---

## 📁 Project Structure

```
src/main/java/.../
├── TaskManagerApplication.java
├── controller/     → TaskController (thin — HTTP only)
├── service/        → TaskService (business logic + the state-machine rule)
├── repository/     → TaskRepository (JpaRepository + custom JPQL)
├── model/          → Task (@Entity)
├── dto/            → TaskRequest / TaskResponse (entities never exposed directly)
└── exception/      → GlobalExceptionHandler, ResourceNotFoundException,
                       InvalidTaskStateException, ErrorResponse

src/main/resources/
├── application.yml        → shared config, Actuator, profile fallback
├── application-dev.yml    → local: verbose, permissive
└── application-prod.yml   → production: quiet, ddl-auto: validate, no silent defaults

src/test/java/.../
├── service/        → Mockito unit tests
├── controller/      → @WebMvcTest
├── repository/      → @DataJpaTest
└── integration/     → Testcontainers end-to-end test

Dockerfile              → multi-stage build (Maven → slim JRE, non-root user)
docker-compose.yml       → app + Postgres, healthchecked, networked, persistent volume
render.yaml / railway.json → cloud deployment configs
.github/workflows/ci-cd.yml → CI (test) + CD (build & push image)
```

---

## 🚫 What's Deliberately Not Included

Being upfront about scope matters more than pretending this is more finished than it is:

- **No authentication/authorization.** This roadmap's Week 5–6 (JWT, Spring Security, role-based access) was not built in this testing/deployment-focused track. Every endpoint here is open. Adding auth is the natural next step before this could handle real user data.
- **No Flyway/Liquibase migrations.** `ddl-auto: validate` in production assumes a schema that already exists and matches the entities — a real deployment needs an actual migration tool to get the schema there in the first place, not Hibernate auto-DDL.
- **No live URL yet.** Deployment configs are ready and documented; the actual account-level deployment step is left to whoever runs this for real (see Week 8 Day 6's README for the honest reasoning).

---

## 🗺 Roadmap Retrospective

This project is the accumulated result of an 8-week, self-paced roadmap:

| Week | Focus |
|---|---|
| 1–2 | Spring Boot core (IOC, DI, beans) + REST fundamentals |
| 3–4 | Spring Data JPA, relationships, JPQL, pagination, transactions |
| 5–6 | *(Security, DTOs, validation, exception handling — this project uses the DTO/validation/exception patterns; JWT auth itself wasn't built in this track)* |
| 7 | Full testing pyramid — Mockito, `@WebMvcTest`, `@DataJpaTest`, Testcontainers |
| 8 | Docker, Docker Compose, production config, CI, CD, cloud deployment |

---

## 📄 License

MIT — use this as a reference, a starting point, or a portfolio piece freely.

---

**Built as a portfolio project.** Feedback, issues, and pull requests welcome.
