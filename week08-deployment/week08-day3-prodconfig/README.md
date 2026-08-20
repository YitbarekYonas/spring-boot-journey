# Week 8, Day 3: Environment Configuration for Production

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-3-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Actuator%20%2B%20Profiles-orange.svg)]()

> **"A container that's 'running' and a container that's 'ready' are two different claims. Actuator lets Docker (and everything downstream of Docker) tell them apart."**

---

## 🎯 Learning Objectives

- ✅ Add Spring Boot Actuator and expose `/actuator/health` (and only what's needed)
- ✅ Split config into a base `application.yml` + `application-dev.yml` + `application-prod.yml`
- ✅ Activate `prod` in Docker via `SPRING_PROFILES_ACTIVE`, with zero code changes
- ✅ Write a Docker-native `HEALTHCHECK` that actually calls the app, not just checks the process is alive
- ✅ Wire that same healthcheck into `docker-compose.yml` so `docker compose ps` reports real health
- ✅ Understand why `ddl-auto` and health detail differ deliberately between dev and prod

---

## 💡 What I Learned Today

### 1. "Running" ≠ "Healthy"

Without a healthcheck, Docker only knows one thing about a container: whether its main process has exited or not. A Spring Boot app can be "running" (the JVM process is alive) while still being completely unable to serve requests — still connecting to the database, still initializing beans, or genuinely stuck. Actuator's `/actuator/health` endpoint is what closes that gap: it's Spring Boot itself reporting "yes, I can actually do my job right now," not just "my process hasn't crashed."

### 2. Locking Down Actuator, Not Just Turning It On

Actuator ships many endpoints (`/actuator/env`, `/actuator/beans`, `/actuator/mappings`, etc.) and most are **disabled by default** for good reason — `/actuator/env` alone can leak every environment variable the app has access to, including secrets, to anyone who can reach it.

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info   # ← explicit allowlist, not "expose everything"
```

Only `health` and `info` are turned on here — exactly what this project needs, nothing that could leak internal state.

### 3. Why `show-details` Differs Between Profiles

```yaml
# application.yml (base / dev)
management.endpoint.health.show-details: always

# application-prod.yml (overrides for prod)
management.endpoint.health.show-details: never
```

`show-details: always` is genuinely useful in dev — you see exactly which component (DB, disk space, etc.) is failing. In prod, handing that same detail to any anonymous request is an information leak (confirms internal architecture, DB status, etc. to an attacker). `never` in prod means `/actuator/health` returns just `{"status":"UP"}` — enough for a load balancer or orchestrator to act on, nothing more.

### 4. Three-File Profile Structure

```
application.yml        ← shared config (Actuator setup, app name, profile fallback)
application-dev.yml     ← local: verbose logging, ddl-auto: update, permissive
application-prod.yml    ← real: quiet logging, ddl-auto: validate, no fallback DB defaults
```

Notice `application-prod.yml`'s datasource block has **no** `${DB_URL:jdbc:postgresql://localhost:...}` fallback — just `${DB_URL}`. In dev, a sensible localhost default is genuinely convenient. In prod, if `DB_URL` isn't set, the app should refuse to start with a clear error — not silently connect to the wrong thing (or nothing).

### 5. `ddl-auto: validate` in Production

```yaml
# application-prod.yml
spring.jpa.hibernate.ddl-auto: validate
```

This is the roadmap's Week 3 Common Mistake, addressed directly: `update`/`create` let Hibernate silently alter your production schema based on your entity classes — exactly the kind of surprise you don't want in prod. `validate` instead just checks the existing schema matches what the entities expect, and fails loudly at startup if it doesn't. Real schema changes belong to a migration tool (Flyway/Liquibase — mentioned in the roadmap, not built in this project) applied deliberately, not to Hibernate auto-DDL.

### 6. Activating the Profile — One Env Var, Zero Rebuilds

```yaml
# docker-compose.yml
environment:
  SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE:-prod}
```

The exact same built image runs completely differently depending on this one variable. Rebuilding the image is never required to switch profiles — that's the entire point of externalizing config instead of hardcoding it into the jar.

### 7. `HEALTHCHECK` in the `Dockerfile`

```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --start-period=15s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1
```

- `--start-period=15s` — a grace window before failed checks count against the container. Spring Boot's own startup (bean initialization, DB connection pool warm-up) takes a few seconds; without this, Docker could mark the container "unhealthy" before it's even finished booting.
- `--spider` (a `wget` flag) — makes the request without downloading/saving a response body; we only care about the HTTP status, not the content.
- Once this is in the image, **`docker ps` itself** shows `(healthy)`/`(unhealthy)`/`(health: starting)` next to the container — visible with zero extra tooling.

### 8. The Same Healthcheck, Now in Compose Too

```yaml
# docker-compose.yml
app:
  healthcheck:
    test: ["CMD", "wget", ..., "http://localhost:8080/actuator/health"]
```

Defining it again here (matching the Dockerfile) means Compose's own `docker compose ps` reports health directly, and — the Day 3 exercise's specific ask — any *other* service added to this compose file later could depend on `app` being healthy the same way `app` already depends on `db` being healthy.

---

## 🐳 Running This Locally

```bash
cp .env.example .env

docker compose up --build

# Check container health directly
docker compose ps
# STATUS column should eventually show "healthy" for both services

# Hit the health endpoint yourself
curl http://localhost:8080/actuator/health
# {"status":"UP"}  — minimal, because SPRING_PROFILES_ACTIVE=prod by default

# Try the app's actual endpoints — same as every prior day
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Verify prod config","status":"TODO","priority":"HIGH","ownerEmail":"student@example.com"}'
```

**To see the dev profile's fuller health output**, edit `.env`:
```
SPRING_PROFILES_ACTIVE=dev
```
then `docker compose up --build` again — `/actuator/health` will now include the full `components` breakdown (DB status, disk space, etc.), and the logs will be visibly more verbose.

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| Exposing every Actuator endpoint (`include: "*"`) | Explicitly allowlist only what's needed (`health,info`) |
| `show-details: always` in production | Use `never` (or `when-authorized` once real auth exists) in prod |
| `ddl-auto: update` in production | Use `validate`; real schema changes go through Flyway/Liquibase |
| A `HEALTHCHECK` that only checks the process is alive (e.g. `CMD true`) | Actually call `/actuator/health` — proves the app can serve requests, not just that the JVM started |
| No `--start-period` on the healthcheck | The container can be marked unhealthy before Spring Boot even finishes booting |
| Baking `SPRING_PROFILES_ACTIVE` into the image | Set it as a runtime env var — the same image should be deployable to any environment |
| Silent `localhost` fallback defaults in `application-prod.yml` | Missing prod config should fail loudly at startup, not silently connect to the wrong thing |

---

## ✅ Day 3 Checklist

- [x] `spring-boot-starter-actuator` added, `/actuator/health` + `/actuator/info` exposed (only those two)
- [x] Base `application.yml` + `application-dev.yml` + `application-prod.yml`
- [x] `SPRING_PROFILES_ACTIVE` set via Compose environment, defaulting to `prod`
- [x] `ddl-auto: validate` in prod, `update` in dev
- [x] `show-details: never` in prod, `always` in dev
- [x] Docker-native `HEALTHCHECK` instruction in the `Dockerfile`, hitting `/actuator/health`
- [x] Matching `healthcheck:` block in `docker-compose.yml` for the `app` service
- [x] Verified `docker compose ps` reports `healthy` for both services

---

**Date**: August 16, 2026
**Status**: ✅ Week 8, Day 3 Complete!
**Next**: Day 4 — CI Basics with GitHub Actions: a `.github/workflows/ci.yml` that checks out the repo, sets up the JDK, builds, and runs exactly the Week 7 test suite on every push.

> *"Actuator doesn't make the app healthier. It makes the app's health legible — to Docker, to a load balancer, to whoever's on call at 2am."*
