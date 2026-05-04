# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Gradle wrapper and build files first (for layer caching)
COPY gradlew gradlew.bat ./
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties ./

# Download dependencies
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon --quiet || true

# Copy source code
COPY src ./src

# Build fat JAR
RUN ./gradlew installDist --no-daemon -x test

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Copy the distribution from builder
COPY --from=builder /app/build/install/emotion-friend-api ./

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 8080

ENTRYPOINT ["./bin/emotion-friend-api"]
