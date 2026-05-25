# VPS Ubuntu — Emotion Friend Deployment Guide

> Tested on **Ubuntu 22.04 LTS**. All commands run as a non-root user with `sudo` privileges.

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Install Docker & Tools](#2-install-docker--tools)
3. [Configure Firewall (UFW)](#3-configure-firewall-ufw)
4. [Clone Project](#4-clone-project)
5. [Create .env File](#5-create-env-file)
6. [Run the Stack](#6-run-the-stack)
7. [Verify Deployment](#7-verify-deployment)
8. [Update to a New Version](#8-update-to-a-new-version)
9. [Useful Commands](#9-useful-commands)
10. [Deploy Checklist](#10-deploy-checklist)

---

## 1. Prerequisites

| Item | Minimum |
|------|---------|
| OS | Ubuntu 22.04 LTS (64-bit) |
| RAM | 1 GB (2 GB recommended) |
| Disk | 10 GB free |
| CPU | 1 vCPU |
| Network | Public IP, inbound port 80 open |

---

## 2. Install Docker & Tools

### 2.1 Update system

```bash
sudo apt-get update && sudo apt-get upgrade -y
```

### 2.2 Install Git & UFW

```bash
sudo apt-get install -y git ufw curl
```

### 2.3 Install Docker Engine (official repository)

```bash
# Add Docker's official GPG key
sudo apt-get install -y ca-certificates gnupg
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg \
  | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg

# Add repository
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
  https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" \
  | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Install Docker Engine + Compose plugin
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io \
  docker-buildx-plugin docker-compose-plugin
```

### 2.4 Allow current user to run Docker without sudo

```bash
sudo usermod -aG docker $USER
newgrp docker          # apply group change to current shell
```

### 2.5 Verify installations

```bash
git    --version       # git 2.x
docker --version       # Docker 24+
docker compose version # Docker Compose v2.x
```

---

## 3. Configure Firewall (UFW)

```bash
# Deny all incoming by default, allow all outgoing
sudo ufw default deny incoming
sudo ufw default allow outgoing

# Allow SSH (keep existing sessions alive)
sudo ufw allow ssh

# Allow HTTP (Nginx entry point)
sudo ufw allow 80/tcp

# Enable firewall
sudo ufw enable

# Verify status
sudo ufw status verbose
```

Expected output:
```
Status: active
To                   Action      From
--                   ------      ----
22/tcp               ALLOW IN    Anywhere
80/tcp               ALLOW IN    Anywhere
```

> **Note**: Port 8080 (backend) and 3306 (MySQL) are intentionally NOT exposed — they
> communicate only on the internal Docker network `backend_net`.

---

## 4. Clone Project

```bash
# Choose a deployment directory
cd /opt
sudo mkdir emotion-friend
sudo chown $USER:$USER emotion-friend

# Clone repository
git clone https://github.com/nguyentrungnghia1802/emotion-friend.git emotion-friend
cd emotion-friend
```

---

## 5. Create .env File

```bash
# Copy the example file
cp .env.example .env

# Fill in real values (never commit this file)
nano .env
```

`.env` content — replace every `change_me_*` value:

```env
MYSQL_ROOT_PASSWORD=<strong-root-password>
MYSQL_DATABASE=emotion_friend
MYSQL_USER=emotion_user
MYSQL_PASSWORD=<strong-app-password>
PORT=8080
```

Password requirements:
- Minimum 16 characters
- Mix of upper/lower case, digits, and symbols
- Different for root and app user

---

## 6. Run the Stack

```bash
# Build images and start all services in background
docker compose up -d --build
```

Docker will start three containers in order:
1. `emotion_friend_mysql` — waits until healthy (healthcheck every 10 s)
2. `emotion_friend_backend` — starts after MySQL is healthy
3. `emotion_friend_nginx` — starts after backend is healthy

Monitor startup progress:

```bash
docker compose ps          # check status of all containers
docker compose logs -f     # stream logs from all containers
docker compose logs backend -f   # backend only
```

---

## 7. Verify Deployment

### 7.1 Check container status

```bash
docker compose ps
```

Expected — all containers `running (healthy)`:
```
NAME                     STATUS                    PORTS
emotion_friend_mysql     Up X minutes (healthy)
emotion_friend_backend   Up X minutes (healthy)
emotion_friend_nginx     Up X minutes             0.0.0.0:80->80/tcp
```

### 7.2 Health endpoint

```bash
# From VPS itself
curl -s http://localhost/health | python3 -m json.tool

# From another machine (replace with your VPS IP)
curl -s http://<VPS_IP>/health
```

Expected response:
```json
{
  "status": "ok",
  "database": "connected",
  "version": "1.0.0"
}
```

### 7.3 API endpoints

```bash
# List emotions
curl -s http://localhost/api/emotions | python3 -m json.tool

# List situations
curl -s http://localhost/api/situations | python3 -m json.tool

# Get progress for user 1
curl -s "http://localhost/api/progress?userId=1" | python3 -m json.tool

# Post an emotion log
curl -s -X POST http://localhost/api/emotion-log \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"emotionId":1,"note":"test"}' \
  | python3 -m json.tool
```

### 7.4 Check logs for errors

```bash
docker compose logs mysql   --tail=20
docker compose logs backend --tail=20
docker compose logs nginx   --tail=20
```

---

## 8. Update to a New Version

```bash
cd /opt/emotion-friend

# 1. Pull latest code
git pull origin main

# 2. Rebuild and restart only changed services (zero-downtime for unchanged)
docker compose up -d --build

# 3. Remove dangling images from old builds
docker image prune -f
```

To roll back to a previous commit:

```bash
git log --oneline -10          # find the commit hash to roll back to
git checkout <commit-hash>     # detach HEAD at that commit
docker compose up -d --build   # redeploy
```

---

## 9. Useful Commands

```bash
# Stop all services (data preserved in volume)
docker compose down

# Stop and delete volumes (DESTRUCTIVE — wipes MySQL data)
docker compose down -v

# Restart a single service
docker compose restart backend

# Open a shell inside a running container
docker compose exec backend  sh
docker compose exec mysql    bash

# Connect to MySQL from within the VPS
docker compose exec mysql mysql -u emotion_user -p emotion_friend

# View disk usage by Docker
docker system df
```

---

## 10. Deploy Checklist

Use this checklist after every new deployment.

### System
- [ ] Ubuntu 22.04 LTS, fully updated
- [ ] Docker Engine 24+ installed
- [ ] Docker Compose v2 plugin available (`docker compose version`)
- [ ] Non-root deploy user in `docker` group

### Security
- [ ] UFW active — only ports 22 and 80 open
- [ ] `.env` file created from `.env.example`
- [ ] All `change_me_*` passwords replaced with strong unique values
- [ ] `.env` NOT committed to Git

### Stack
- [ ] `docker compose up -d --build` completed without error
- [ ] All 3 containers in `running (healthy)` state (`docker compose ps`)
- [ ] `curl http://localhost/health` returns `{"status":"ok","database":"connected",...}`
- [ ] `curl http://localhost/api/emotions` returns array of 6 emotions

### API Smoke Tests
- [ ] `GET  /health`               → 200
- [ ] `GET  /api/emotions`         → 200, array
- [ ] `GET  /api/situations`       → 200, array
- [ ] `GET  /api/progress?userId=1` → 200, object
- [ ] `POST /api/emotion-log`      → 201

### Post-Deploy
- [ ] Logs checked — no ERROR or WARN in last 50 lines
- [ ] Old dangling images cleaned (`docker image prune -f`)
