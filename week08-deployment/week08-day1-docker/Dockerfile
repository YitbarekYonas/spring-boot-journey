# ══════════════════════════════════════════════════════════════════════════
# STAGE 1 — Build
# ══════════════════════════════════════════════════════════════════════════
# Uses the OFFICIAL Maven image (bundles a full JDK + Maven itself) — but
# this entire stage is discarded from the final image. Only its OUTPUT (the
# built .jar) gets copied forward. This is the whole point of a multi-stage
# build: you get all the tools you need to compile, without shipping any of
# them to production.
#
# (Using the maven:*-eclipse-temurin-21 image here instead of a bare JDK +
# Maven wrapper — this project doesn't commit a mvnw/.mvn wrapper, and the
# official Maven image is the simplest way to get a working `mvn` without
# needing one. If your own project has a wrapper committed, swap this stage
# for `FROM eclipse-temurin:21-jdk-jammy` + `COPY mvnw .` + `./mvnw ...`.)
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml FIRST, before the source code. Docker caches each layer — if
# pom.xml hasn't changed, Docker reuses the cached dependency-download layer
# on the next build instead of re-downloading the entire dependency tree
# every time you change one line of Java code. This ordering is the single
# biggest lever for fast rebuilds.
COPY pom.xml .

# Download dependencies into a cached layer, separate from compiling source.
RUN mvn dependency:go-offline -B

# NOW copy the actual source code — this layer invalidates on every code
# change, but the (expensive) dependency layer above stays cached.
COPY src src

# Package the app, skipping tests here — tests already ran in CI (Day 4)
# before this image is ever built. Re-running them inside the image build
# just slows down every build for no new information.
RUN mvn clean package -DskipTests -B

# ══════════════════════════════════════════════════════════════════════════
# STAGE 2 — Run
# ══════════════════════════════════════════════════════════════════════════
# A slim JRE image, not a JDK — production doesn't need a compiler, a build
# tool, or any of Stage 1's tooling. Smaller image, smaller attack surface,
# faster pulls/deploys. (Common Mistake from the roadmap: shipping the full
# JDK to production — avoided here on purpose.)
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Run as a non-root user — a container escape shouldn't hand an attacker
# root inside the container for free. Small step, real hardening.
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

# Copy ONLY the built jar from Stage 1 — nothing else from the build stage
# (no source, no Maven cache, no JDK) makes it into this final image.
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# exec form (JSON array), not shell form — ensures Java receives OS signals
# (like SIGTERM on `docker stop`) directly, so Spring Boot can shut down
# gracefully instead of being killed abruptly after a timeout.
ENTRYPOINT ["java", "-jar", "app.jar"]
