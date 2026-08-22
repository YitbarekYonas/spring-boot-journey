# ══════════════════════════════════════════════════════════════════════════
# STAGE 1 — Build
# ══════════════════════════════════════════════════════════════════════════
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src src
RUN mvn clean package -DskipTests -B

# ══════════════════════════════════════════════════════════════════════════
# STAGE 2 — Run
# ══════════════════════════════════════════════════════════════════════════
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# wget isn't guaranteed to be present in the slim JRE base image — install
# it explicitly for the HEALTHCHECK below, then clean up apt's cache in the
# same layer to avoid bloating the image with package-list files we don't
# need afterward.
RUN apt-get update && apt-get install -y --no-install-recommends wget \
    && rm -rf /var/lib/apt/lists/*

RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# ── HEALTHCHECK — new this project ──────────────────────────────────────────
# Docker itself now knows whether the app inside this container is actually
# healthy, not just "the process is running." `docker ps` will show
# "healthy"/"unhealthy"/"starting" for this container, and orchestrators
# (Compose's `condition: service_healthy`, Kubernetes readiness/liveness
# probes, etc.) can act on it directly — this is exactly the endpoint
# Actuator gives us for that purpose.
#
# --interval: how often to check | --timeout: how long to wait per check
# --start-period: grace time before failing checks count against the
#   container (Spring Boot's own startup takes a few seconds — don't mark
#   the container unhealthy just because it hasn't finished booting yet)
# --retries: consecutive failures before Docker marks it "unhealthy"
HEALTHCHECK --interval=30s --timeout=3s --start-period=15s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
