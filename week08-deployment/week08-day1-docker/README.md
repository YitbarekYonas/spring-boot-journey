# Week 8, Day 1: Docker Fundamentals

[![Status](https://img.shields.io/badge/Status-Completed-brightgreen.svg)]()
[![Day](https://img.shields.io/badge/Day-1-blue.svg)]()
[![Topic](https://img.shields.io/badge/Topic-Docker-orange.svg)]()

> **"Same jar, same JVM, same behavior — anywhere Docker runs. That's the whole promise."**

---

## 🎯 Learning Objectives

- ✅ Understand the difference between an image and a container
- ✅ Write a multi-stage `Dockerfile` for a Spring Boot app
- ✅ Understand why the final image uses a JRE, not a JDK
- ✅ Understand Docker's layer caching and why `COPY` order matters
- ✅ Build and run the image locally, hit it with real HTTP requests
- ✅ Understand why the container runs as a non-root user

---

## 💡 What I Learned Today

### 1. Image vs Container

An **image** is a read-only template — the built artifact, like a class in Java. A **container** is a running instance of that image — like an object instantiated from the class. `docker build` produces the image; `docker run` produces a container.

### 2. Why Multi-Stage?

A single-stage `Dockerfile` that both compiles the app AND runs it would need the full JDK, Maven, and every dependency's build-time artifacts sitting in your *production* image — none of which the running app actually needs.

```dockerfile
FROM maven:3.9.9-eclipse-temurin-21 AS build   # Stage 1: has Maven + full JDK
# ... compile the app ...

FROM eclipse-temurin:21-jre-jammy              # Stage 2: just a JRE
COPY --from=build /app/target/*.jar app.jar    # only the .jar crosses over
```

Everything in Stage 1 — Maven itself, the downloaded dependency `.jar`s, the compiler — is **discarded**. Only the final built `app.jar` is copied into Stage 2. The result: a much smaller, much leaner final image.

### 3. Why JRE, Not JDK, for the Final Image

The roadmap flags this explicitly as a Common Mistake — and it's an easy one to make, since it's tempting to just reuse the same JDK image for both stages. A JRE (Java **Runtime** Environment) can *run* compiled `.class`/`.jar` files but has no compiler (`javac`) and no build tooling. That's exactly what a running production app needs — nothing more. Smaller image, smaller attack surface, faster to pull and deploy.

### 4. Layer Caching — Why `COPY pom.xml` Happens Before `COPY src`

Docker builds an image as a stack of layers, and caches each one. If a layer's inputs haven't changed since the last build, Docker reuses the cached result instead of re-running that step.

```dockerfile
COPY pom.xml .
RUN mvn dependency:go-offline -B   # ← this layer is cached...

COPY src src                        # ...as long as THIS hasn't changed
RUN mvn clean package -DskipTests -B
```

If you copied *all* the source (including `pom.xml`) in one `COPY . .` step, then changing a single line of Java would invalidate the cache for the dependency-download step too — forcing Maven to redownload the entire dependency tree on every single build. Splitting `pom.xml` out first means dependency resolution stays cached across ordinary code changes, and only reruns when `pom.xml` itself changes.

### 5. Running as a Non-Root User

```dockerfile
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring
```

By default, a container runs as `root` unless told otherwise. If an attacker ever found a way to escape the app process, running as root inside the container is a meaningfully worse starting position than running as an unprivileged user. This costs three lines and is worth doing on every image.

### 6. `ENTRYPOINT` — Exec Form vs Shell Form

```dockerfile
# Exec form (what we used) — Java gets OS signals directly
ENTRYPOINT ["java", "-jar", "app.jar"]

# Shell form (avoid) — Java runs as a child of /bin/sh, which intercepts signals
ENTRYPOINT java -jar app.jar
```

With the shell form, `docker stop` sends `SIGTERM` to the shell, not to the Java process directly — the shell often doesn't forward it, so Docker ends up waiting out the full grace period and then force-killing the container with `SIGKILL`. Spring Boot never gets the chance to shut down its connections and threads gracefully. The exec form (JSON array syntax) avoids the shell entirely, so Java receives the signal itself.

---

## 🐳 Building and Running Locally

```bash
# Build the image
docker build -t task-manager:day1 .

# Run it, mapping container port 8080 to host port 8080
docker run -p 8080:8080 task-manager:day1

# In another terminal — hit it with real requests
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
        "title": "Dockerize the app",
        "status": "IN_PROGRESS",
        "priority": "HIGH",
        "ownerEmail": "student@example.com"
      }'

curl http://localhost:8080/api/tasks
```

Or import the same requests into Postman against `http://localhost:8080` — everything from Week 2–7 works identically; the app doesn't know or care that it's running inside a container.

**Note:** this image uses H2 (in-memory), deliberately — see the note in `application.yml`. That means data resets every time the container restarts, and it means today's exercise has exactly one moving part (the Dockerfile) instead of two. Wiring this app to a real Postgres container running *alongside* it — with proper networking and persistent volumes — is tomorrow's exercise (Day 2, Docker Compose).

---

## ❌ Common Mistakes

| Mistake | Fix |
|---------|-----|
| Using the full JDK image for the final stage | Use a slim JRE image (`eclipse-temurin:21-jre-jammy`) — smaller, faster, more secure |
| `COPY . .` before installing dependencies | Copy `pom.xml` first, resolve dependencies, THEN copy source — preserves the dependency cache across code changes |
| Running the container as root | Add a non-root user, `USER` before the app starts |
| Shell-form `ENTRYPOINT` | Use exec form (`["java", "-jar", "app.jar"]`) so signals reach the JVM directly |
| Not using `.dockerignore` | `target/`, `.git/`, IDE files bloat the build context and slow every build |
| Running tests inside the image build | Tests belong in CI (Day 4), before the image is built — not baked into every build |
| Hardcoding `EXPOSE`/ports without checking what the app actually binds to | `EXPOSE 8080` is documentation, not enforcement — make sure it matches `server.port` |

---

## ✅ Day 1 Checklist

- [x] Multi-stage `Dockerfile` (build stage + run stage)
- [x] Build stage uses a full JDK/Maven image; final stage uses a slim JRE
- [x] `pom.xml` copied and dependencies resolved before source code, for layer caching
- [x] Tests skipped during image build (`-DskipTests`) — that's CI's job
- [x] Non-root user (`USER spring:spring`)
- [x] Exec-form `ENTRYPOINT`
- [x] `.dockerignore` excluding `target/`, `.git/`, IDE files
- [x] Built and ran locally, hit every endpoint via `curl`/Postman

---

**Date**: August 15, 2026
**Status**: ✅ Week 8, Day 1 Complete!
**Next**: Day 2 — Docker Compose: run this app alongside a real Postgres container, wired together over Docker's internal networking, with a persistent volume for the database.

> *"The Dockerfile is a recipe. `docker build` bakes it once; `docker run` serves it as many times as you want, identically, anywhere."*
