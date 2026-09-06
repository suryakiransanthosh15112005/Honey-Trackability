# ============================================================
# HoneyChain Backend — Dockerfile
# Multi-stage build: Maven build → Java 17 runtime
# ============================================================

# ── Stage 1: Build ──────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy dependency manifests first for layer caching
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

# Pre-download dependencies (cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Copy source and build the JAR (tests run via CI/CD; skip here for speed)
COPY src/ src/
RUN mvn package -DskipTests -B

# ── Stage 2: Runtime ────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -S honeychain && adduser -S honeychain -G honeychain

# Create upload directory and set permissions
RUN mkdir -p /app/uploads/qr && chown -R honeychain:honeychain /app

# Copy JAR from build stage
COPY --from=build /app/target/honeychain-backend-*.jar app.jar

# Use non-root user
USER honeychain

# Expose REST API port
EXPOSE 8080

# Health check — wait for Spring Boot to be ready
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=5 \
  CMD wget -qO- http://localhost:8080/api/health || exit 1

# Run the application
# SPRING_PROFILES_ACTIVE is injected via Docker Compose environment
ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]
