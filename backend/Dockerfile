# ── Stage 1: build fat JAR ─────────────────────────────────────────────────────
FROM gradle:8.5-jdk17-alpine AS builder
WORKDIR /app
COPY . .
RUN gradle shadowJar --no-daemon -q

# ── Stage 2: minimal JRE runtime ──────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/emotion-friend-backend-all.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
