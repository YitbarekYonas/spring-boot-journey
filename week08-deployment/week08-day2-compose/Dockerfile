# ══════════════════════════════════════════════════════════════════════════
# STAGE 1 — Build
# ══════════════════════════════════════════════════════════════════════════
# Same multi-stage pattern as Day 1 — see that day's README for the full
# reasoning on JDK-vs-JRE, layer caching order, and exec-form ENTRYPOINT.
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

RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
