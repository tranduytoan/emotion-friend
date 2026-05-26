# Emotion Friend — VPS Deployment Guide

> Production deployment using **Docker Compose** on Ubuntu 22.04 LTS.
> For a step-by-step first-time server setup see [vps-deployment-guide.md](vps-deployment-guide.md).

---

## Table of Contents

1. [Server Requirements](#1-server-requirements)
2. [Deployment Architecture](#2-deployment-architecture)
3. [Server Directory Structure](#3-server-directory-structure)
4. [Environment Variables (.env)](#4-environment-variables-env)
5. [First Deploy](#5-first-deploy)
6. [Health Verification](#6-health-verification)
7. [Update to New Version](#7-update-to-new-version)
8. [Basic Rollback](#8-basic-rollback)
9. [Troubleshooting](#9-troubleshooting)

---

## 1. Server Requirements

| Resource | Minimum | Recommended |
|----------|---------|-------------|
| OS       | Ubuntu 22.04 LTS 64-bit | Ubuntu 22.04 LTS |
| CPU      | 1 vCPU | 2 vCPU |
| RAM      | 1 GB | 2 GB |
| Disk     | 10 GB | 20 GB SSD |
| Network  | Public IPv4, port 80 open | + port 443 for HTTPS |

**Required software** (see [vps-deployment-guide.md](vps-deployment-guide.md) for install commands):

- Docker Engine 24+
- Docker Compose v2 plugin
- Git 2.x
- UFW firewall

---

## 2. Deployment Architecture

```
Internet
    │
    ▼  port 80 (HTTP)  ─ or ─  port 443 (HTTPS)
┌─────────────────────────────────────────────────────┐
│                   Docker network: backend_net        │
│                                                     │
│  ┌─────────────┐     proxy      ┌───────────────┐  │
│  │   nginx     │ ─────────────► │    backend    │  │
│  │ :80 / :443  │                │  Ktor :8080   │  │
│  └─────────────┘                └───────┬───────┘  │
│                                         │ JDBC      │
│                                 ┌───────▼───────┐  │
│                                 │     mysql     │  │
│                                 │  MySQL 8 :3306│  │
│                                 └───────────────┘  │
└─────────────────────────────────────────────────────┘
         Volumes: mysql_data (persistent)
```

**Key design decisions:**
- MySQL port 3306 and backend port 8080 are **not** exposed to the host — internal traffic only
- Only Nginx (port 80/443) faces the internet
- Backend starts only after MySQL passes its healthcheck
- Nginx starts only after backend passes its healthcheck
- `restart: unless-stopped` keeps containers running after VPS reboot

---

## 3. Server Directory Structure

```
/opt/emotion-friend/              ← VPS deploy root
├── .env                          ← secrets (git-ignored, never committed)
├── .env.example                  ← template (committed)
├── docker-compose.yml            ← base stack
├── docker-compose.https.yml      ← HTTPS override (optional)
├── backend-api/
│   ├── Dockerfile                ← multi-stage build (Gradle → JRE)
│   └── src/...
├── nginx/
│   ├── nginx.conf                ← HTTP config
│   └── nginx-https.conf          ← HTTPS config (optional)
└── scripts/
    └── setup-https.sh            ← one-shot HTTPS setup script
```

---

## 4. Environment Variables (.env)

```bash
# On VPS
cp .env.example .env
nano .env          # fill in real values
```

`.env` content:

```env
# MySQL
MYSQL_ROOT_PASSWORD=<strong-unique-root-password>
MYSQL_DATABASE=emotion_friend
MYSQL_USER=emotion_user
MYSQL_PASSWORD=<strong-unique-app-password>

# Backend (port inside container — do not change unless you edit docker-compose.yml)
PORT=8080
```

**Password rules:**
- Minimum 16 characters
- Mix of upper/lowercase, digits, and symbols
- Root password and app password must be **different**
- Never commit `.env` — it is already in `.gitignore`

---

## 5. First Deploy

```bash
# 1. Navigate to project directory
cd /opt/emotion-friend

# 2. Ensure .env exists with real values
cat .env | grep -v PASSWORD   # spot-check without revealing secrets

# 3. Build images and start all services
docker compose up -d --build

# 4. Monitor startup (Ctrl+C to stop following)
docker compose logs -f
```

Expected startup order and time:
| Service | Startup time |
|---------|-------------|
| mysql | ~30 s (healthcheck start_period) |
| backend | ~20 s after MySQL healthy |
| nginx | ~5 s after backend healthy |

---

## 6. Health Verification

```bash
# All containers running and healthy?
docker compose ps

# API health check
curl -s http://localhost/health
# Expected: {"status":"ok","database":"connected","version":"1.0.0"}

# Emotion list
curl -s http://localhost/api/emotions

# Situation list
curl -s http://localhost/api/situations

# Progress for user 1
curl -s "http://localhost/api/progress?userId=1"

# Post emotion log
curl -s -X POST http://localhost/api/emotion-log \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"emotionId":1,"note":"deploy test"}'
```

For HTTPS:
```bash
curl -s https://your-domain.com/health
curl -I https://your-domain.com/health   # check SSL headers
```

---

## 7. Update to New Version

```bash
cd /opt/emotion-friend

# Pull latest code
git fetch origin main
git reset --hard origin/main

# Rebuild and restart (zero-downtime for unchanged services)
docker compose up -d --build

# Verify new version is running
curl -s http://localhost/health

# Clean up old images
docker image prune -f
```

> `git reset --hard origin/main` is used instead of `git pull` to avoid merge
> conflicts in a deployment context. The VPS should never have local commits.

---

## 8. Basic Rollback

### Roll back to previous commit

```bash
cd /opt/emotion-friend

# Find the commit to roll back to
git log --oneline -10

# Pin to that commit
git checkout <commit-hash>

# Rebuild with that version
docker compose up -d --build

# Verify
curl -s http://localhost/health
```

### Roll back to a specific Git tag (if tags are used)

```bash
git checkout tags/v1.0.0
docker compose up -d --build
```

### Emergency: restart only backend

```bash
docker compose restart backend
docker compose logs backend --tail=30
```

### Emergency: wipe database and re-import schema

> ⚠️ **DESTRUCTIVE** — all data will be lost. Only use in development/staging.

```bash
docker compose down -v          # stops containers AND deletes volumes
docker compose up -d --build    # fresh start, schema.sql auto-imported
```

---

## 9. Troubleshooting

### Container won't start

```bash
docker compose ps                          # check status
docker compose logs <service> --tail=50   # read error logs
```

### MySQL healthcheck keeps failing

```bash
docker compose logs mysql --tail=30
# Common causes:
# - Wrong MYSQL_ROOT_PASSWORD in .env
# - Insufficient disk space (df -h)
# - Port 3306 already in use on host (netstat -tlnp | grep 3306)
```

### Backend can't connect to MySQL

```bash
docker compose logs backend --tail=30
# Check DATABASE_URL matches jdbc:mysql://mysql:3306/<db>?...
# Check DATABASE_USER / DATABASE_PASSWORD match .env
# Ensure mysql container is healthy before backend starts
```

### Nginx returns 502 Bad Gateway

```bash
docker compose logs nginx   --tail=20
docker compose logs backend --tail=20
# Backend may still be starting — wait 30 s and retry
# Or: docker compose restart backend
```

### Port 80 already in use

```bash
sudo lsof -i :80
# If another process is using port 80, stop it first:
# sudo systemctl stop apache2   (or nginx running outside Docker)
```

### Out of disk space

```bash
df -h
docker system df
docker image prune -f       # remove dangling images
docker volume prune -f      # WARNING: removes unused volumes (not mysql_data if in use)
```

### View all container resource usage

```bash
docker stats --no-stream
```
