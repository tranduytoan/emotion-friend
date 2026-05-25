# Emotion Friend — Backend (Ktor)

Ktor 2.3.12 · Kotlin 1.9.24 · MySQL 8 · HikariCP

## Architecture

```
backend/
├── build.gradle.kts
├── Dockerfile
└── src/main/kotlin/com/emotionfriend/
    ├── Application.kt           ← entry point, wires everything
    ├── config/
    │   └── DatabaseConfig.kt   ← HikariCP pool; null = mock fallback
    ├── models/
    │   └── Models.kt           ← serializable data classes
    ├── repositories/           ← raw JDBC + mock fallback
    ├── services/               ← thin layer over repositories
    └── routes/                 ← Ktor routing functions
```

## Endpoints

| Method | Path               | Description                            |
|--------|--------------------|----------------------------------------|
| GET    | `/health`          | Server + DB status                     |
| GET    | `/api/emotions`    | List all 6 core emotions               |
| GET    | `/api/situations`  | List all situation cards               |
| GET    | `/api/progress`    | User progress (`?userId=1`)            |
| POST   | `/api/emotion-log` | Log today's emotion (JSON body)        |

### Sample requests

```bash
# Health check
curl http://localhost:8080/health

# Emotions
curl http://localhost:8080/api/emotions

# Situations
curl http://localhost:8080/api/situations

# Progress for user 1
curl "http://localhost:8080/api/progress?userId=1"

# Log an emotion
curl -X POST http://localhost:8080/api/emotion-log \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"emotionId":3,"note":"Hôm nay hơi buồn"}'
```

## Environment variables

| Variable      | Required | Default        | Description              |
|---------------|----------|----------------|--------------------------|
| `DB_HOST`     | No*      | —              | MySQL host               |
| `DB_PORT`     | No       | `3306`         | MySQL port               |
| `DB_NAME`     | No       | `emotion_friend` | Database name          |
| `DB_USER`     | No*      | —              | MySQL user               |
| `DB_PASSWORD` | No*      | —              | MySQL password           |
| `PORT`        | No       | `8080`         | HTTP listen port         |

*If any of `DB_HOST`, `DB_USER`, or `DB_PASSWORD` is missing, the server starts
with **mock data** and still serves all endpoints — useful for demo without a DB.

## Run locally

### Prerequisites
- JDK 17+
- Gradle 8.5+ (or use the wrapper after `gradle wrapper`)

```bash
cd backend

# Without DB (mock data — works immediately)
gradle run

# With DB
DB_HOST=localhost DB_USER=root DB_PASSWORD=secret gradle run
```

### Run with Docker

```bash
cd backend
docker build -t emotion-friend-backend .
docker run -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_USER=root \
  -e DB_PASSWORD=secret \
  emotion-friend-backend
```

## Mock data fallback

When env vars are missing **or** the DB is unreachable, all repositories
return embedded mock data that matches the seed in `database/schema.sql`.
The `/health` response shows `"database": "mock"` vs `"database": "connected"`.

## Notes for teammates

- This backend is **not** a replacement for Room (Toàn's local DB).
- It is a **sync/cloud endpoint** for future parent dashboard / progress backup.
- Auth is intentionally not implemented in this MVP skeleton.
- To add a new endpoint: add a `*Repository` → `*Service` → `*Routes` file
  and register the route in `Application.kt`.
