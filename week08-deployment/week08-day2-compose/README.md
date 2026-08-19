# Week 8, Day 2: Docker Compose

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-2-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Docker%20Compose-orange.svg)]()

> **"One command, two containers, one working system — networked, sequenced, and persistent."**

---

## 🎯 Learning Objectives

- ✅ Define app + database as services in a single `docker-compose.yml`
- ✅ Understand Compose's automatic internal networking (service name = hostname)
- ✅ Use environment variables for all config — externalized via `.env`, never hardcoded
- ✅ Use a named volume so database data survives container restarts
- ✅ Use a healthcheck + `depends_on: condition: service_healthy` to avoid startup-order race conditions
- ✅ Bring the whole system up and down with one command

---

## 💡 What I Learned Today

### 1. From One Container to Two, Talking to Each Other

Day 1 ran a single container (the app) with an in-memory H2 database — deliberately, to keep that day's Dockerfile the only new concept. Day 2 adds a second container (Postgres) and the actual point of Compose: making two containers **find and talk to each other** without any manual networking setup.

### 2. Service Name = Hostname (The Networking Trick)

```yaml
services:
  db:
    image: postgres:16-alpine
  app:
    environment:
      DB_URL: jdbc:postgresql://db:5432/${POSTGRES_DB:-taskdb}
```

Notice the hostname in `DB_URL` is `db` — not `localhost`, not an IP address. Compose automatically creates an internal network for all services defined in the same file, and gives each one a DNS entry matching its **service name**. The `app` container can reach the `db` container just by using the name `db`, from anywhere inside its own code, with zero manual network configuration. This is the single biggest "aha" of Docker Compose.

### 3. Two Different Ports Doing Two Different Jobs

```yaml
db:
  ports:
    - "5432:5432"   # host:container — for YOU to connect a DB client from outside Docker
```

The app never touches this host-mapped port at all — it talks to `db:5432` entirely over the *internal* Docker network. The host port mapping exists purely so you (a human, with psql or a GUI DB tool) can peek into the database from your own machine while developing.

### 4. Healthchecks — Fixing the Classic Startup Race

Without a healthcheck, `depends_on: db` only guarantees the `db` **container process has started** — not that Postgres inside it is actually ready to accept connections yet. Those two moments can be seconds apart, and the app container will often win that race and crash on its first connection attempt.

```yaml
db:
  healthcheck:
    test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER:-postgres} -d ${POSTGRES_DB:-taskdb}"]
    interval: 5s
    timeout: 5s
    retries: 5

app:
  depends_on:
    db:
      condition: service_healthy   # ← waits for the healthcheck, not just "container exists"
```

`pg_isready` is Postgres' own tool for exactly this check. Compose polls it on the interval given, and only starts the `app` container once `db` reports genuinely healthy.

### 5. Named Volumes — Why Data Survives Restarts

```yaml
services:
  db:
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

Containers are meant to be disposable — `docker compose down` and `docker compose up` again should give you a working system, not necessarily the same *filesystem* inside the container. Postgres' data files live at `/var/lib/postgresql/data` inside the container by default; mapping a **named volume** onto that path means Docker manages that data outside the container's own disposable filesystem. Recreate the container as many times as you want — the volume, and the data in it, persists until you explicitly remove it (`docker compose down -v`).

### 6. Environment Variables via `.env` — Not Hardcoded

```
docker-compose.yml  →  reads ${POSTGRES_PASSWORD:-postgres}
.env (gitignored)   →  supplies the real value
.env.example (committed) → shows the shape, with fake placeholder values
```

This is the roadmap's Common Mistake #1 for Week 8, avoided by construction: nothing in `docker-compose.yml` or the app's `application.yml` has a real credential hardcoded anywhere. `.env` is in `.gitignore`; only `.env.example` (with obviously-fake defaults) gets committed.

---

## 🐳 Running This Locally

```bash
# One-time setup
cp .env.example .env
# (edit .env if you want non-default values)

# Build the app image AND start both services, networked, in the right order
docker compose up --build

# In another terminal — same requests as Day 1, now backed by real Postgres
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
        "title": "Wire up Compose",
        "status": "IN_PROGRESS",
        "priority": "HIGH",
        "ownerEmail": "student@example.com"
      }'

curl http://localhost:8080/api/tasks

# Stop everything (data in the volume survives)
docker compose down

# Bring it back up — same data still there
docker compose up

# Full reset, including wiping the database volume
docker compose down -v
```

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| Using `localhost` as the DB hostname from inside the app container | Use the **service name** (`db`) — `localhost` inside a container refers to the container itself, not its neighbor |
| `depends_on: db` with no healthcheck | Add a `healthcheck` + `condition: service_healthy`, or the app can start before Postgres is ready |
| No named volume for Postgres data | Every `docker compose down` silently wipes your data without one |
| Committing a real `.env` file | Commit only `.env.example`; put `.env` in `.gitignore` |
| Exposing the DB port unnecessarily in a real deployment | Fine for local dev (lets you inspect data); remove `ports:` on `db` entirely in a production compose file where only `app` should reach it |
| Forgetting `--build` after changing app code | `docker compose up` alone reuses the last-built image; `--build` forces a rebuild |

---

## ✅ Day 2 Checklist

- [x] `docker-compose.yml` defining `app` + `db` services
- [x] App connects to the DB via service name (`db`), not `localhost`
- [x] All credentials externalized via `.env` / `${VAR:-default}` syntax — nothing hardcoded
- [x] `.env` gitignored; `.env.example` committed with placeholder values
- [x] Named volume for Postgres data persistence across restarts
- [x] Healthcheck on `db` + `depends_on: condition: service_healthy` on `app`
- [x] Verified with `docker compose up` → real HTTP requests → `docker compose down` → `docker compose up` → data still there

---

**Date**: August 15, 2026
**Status**: ✅ Week 8, Day 2 Complete!
**Next**: Day 3 — Environment Configuration for Production: Spring Boot Actuator health checks, wiring `/actuator/health` into Compose's own healthcheck for the `app` service itself, and activating `application-prod.yml` inside the container.

> *"Compose doesn't make Docker easier by hiding complexity — it makes it easier by giving the complexity (networking, ordering, persistence) one declarative place to live."*
