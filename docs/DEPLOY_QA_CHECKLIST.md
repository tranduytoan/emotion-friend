# Emotion Friend — Deploy QA Checklist

> Run this checklist **after every production deployment** to confirm the system is healthy.
> Mark each item ✅ Pass / ❌ Fail / ⚠️ Warn.

---

## How to Use

1. SSH into VPS: `ssh <user>@<VPS_IP>`
2. Go to project root: `cd /opt/emotion-friend`
3. Run each verification command
4. Fill in the **Status** column
5. All Critical items must be ✅ before going live

---

## Section A — Infrastructure

| ID | Hạng mục | Kết quả mong đợi | Command kiểm tra | Trạng thái |
|----|----------|-----------------|-----------------|-----------|
| A1 | VPS SSH access | SSH connects without error | `ssh <user>@<VPS_IP> echo ok` | ⬜ |
| A2 | Docker Engine running | `Docker version` prints version | `docker --version` | ⬜ |
| A3 | Docker Compose v2 available | `Docker Compose version` prints v2.x | `docker compose version` | ⬜ |
| A4 | UFW active, ports correct | Status active, only 22 + 80 open | `sudo ufw status verbose` | ⬜ |
| A5 | Sufficient disk space | < 85% disk usage | `df -h /` | ⬜ |
| A6 | .env file exists (not .env.example) | File exists, contains real values | `test -f .env && echo exists` | ⬜ |

---

## Section B — Docker Containers

| ID | Hạng mục | Kết quả mong đợi | Command kiểm tra | Trạng thái |
|----|----------|-----------------|-----------------|-----------|
| B1 | All 3 containers running | `Up X minutes` for all 3 | `docker compose ps` | ⬜ |
| B2 | MySQL container healthy | Status shows `(healthy)` | `docker compose ps mysql` | ⬜ |
| B3 | Backend container healthy | Status shows `(healthy)` | `docker compose ps backend` | ⬜ |
| B4 | Nginx container running | Status shows `Up` | `docker compose ps nginx` | ⬜ |
| B5 | MySQL data volume exists | Volume listed | `docker volume ls \| grep mysql_data` | ⬜ |
| B6 | No error logs in last 5 min | No ERROR / FATAL lines | `docker compose logs --since=5m \| grep -i error` | ⬜ |

---

## Section C — API Endpoints

| ID | Hạng mục | Kết quả mong đợi | Command kiểm tra | Trạng thái |
|----|----------|-----------------|-----------------|-----------|
| C1 | GET /health — status ok | `{"status":"ok",...}` HTTP 200 | `curl -s http://localhost/health` | ⬜ |
| C2 | GET /health — database connected | `"database":"connected"` | `curl -s http://localhost/health \| grep connected` | ⬜ |
| C3 | GET /api/emotions — returns array | JSON array, length > 0 | `curl -s http://localhost/api/emotions` | ⬜ |
| C4 | GET /api/situations — returns array | JSON array, length > 0 | `curl -s http://localhost/api/situations` | ⬜ |
| C5 | GET /api/progress?userId=1 — returns data | JSON object with progress fields | `curl -s "http://localhost/api/progress?userId=1"` | ⬜ |
| C6 | POST /api/emotion-log — returns 201 | HTTP 201 Created | `curl -s -o /dev/null -w "%{http_code}" -X POST http://localhost/api/emotion-log -H "Content-Type: application/json" -d '{"userId":1,"emotionId":1}'` | ⬜ |
| C7 | Nginx proxies correctly (no 502) | No 502 Bad Gateway | `curl -I http://localhost/health` | ⬜ |

---

## Section D — HTTPS (if configured)

| ID | Hạng mục | Kết quả mong đợi | Command kiểm tra | Trạng thái |
|----|----------|-----------------|-----------------|-----------|
| D1 | Port 443 open | Connection accepted | `curl -I https://your-domain.com/health` | ⬜ |
| D2 | TLS certificate valid | Issuer = Let's Encrypt, not expired | `openssl s_client -connect your-domain.com:443 </dev/null 2>&1 \| grep "issuer\|Verify"` | ⬜ |
| D3 | HTTP → HTTPS redirect | 301 redirect to https:// | `curl -I http://your-domain.com/health` | ⬜ |
| D4 | HSTS header present | `Strict-Transport-Security` header in response | `curl -sI https://your-domain.com/health \| grep Strict` | ⬜ |

---

## Section E — Android App

| ID | Hạng mục | Kết quả mong đợi | Cách kiểm tra | Trạng thái |
|----|----------|-----------------|--------------|-----------|
| E1 | App installs on device | APK installs without error | Install from CI artifact or `adb install app-debug.apk` | ⬜ |
| E2 | App opens without crash | MainActivity loads | Launch app, check no crash dialog | ⬜ |
| E3 | Navigation works | Can move between screens | Tap through Home → LearnEmotion → Situation | ⬜ |
| E4 | Android calls /api/emotions | Emotion list loads from backend | Open Learn Emotion screen, verify data loads | ⬜ |
| E5 | Offline fallback works | App usable when backend unreachable | Disable WiFi, verify app shows local data | ⬜ |
| E6 | BASE_URL points to VPS | AppConfig.BASE_URL = production URL | Review `AppConfig.kt` before release build | ⬜ |

---

## Section F — Container Restart Resilience

| ID | Hạng mục | Kết quả mong đợi | Cách kiểm tra | Trạng thái |
|----|----------|-----------------|--------------|-----------|
| F1 | Backend auto-restarts on crash | Container restarts within 15 s | `docker compose kill backend && sleep 20 && docker compose ps backend` | ⬜ |
| F2 | Stack survives VPS reboot | All containers running after reboot | `sudo reboot` → wait 2 min → SSH back → `docker compose ps` | ⬜ |
| F3 | MySQL data persists across restart | Data still present after `docker compose restart` | Restart stack, call `/api/emotions` — returns same data | ⬜ |

---

## Section G — CI/CD

| ID | Hạng mục | Kết quả mong đợi | Cách kiểm tra | Trạng thái |
|----|----------|-----------------|--------------|-----------|
| G1 | Android CI passes on main | All steps green in GitHub Actions | GitHub → Actions → Android CI | ⬜ |
| G2 | Debug APK artifact uploaded | Artifact available for download | GitHub → Actions → workflow run → Artifacts | ⬜ |
| G3 | Deploy workflow runs on push to main | deploy-backend.yml triggers | Push a small change to backend/ → watch Actions | ⬜ |
| G4 | Deployment completes successfully | All SSH deploy steps green | GitHub → Actions → Deploy Backend to VPS | ⬜ |

---

## Summary

| Section | Total | Pass | Fail | Warn |
|---------|-------|------|------|------|
| A — Infrastructure | 6 | | | |
| B — Docker Containers | 6 | | | |
| C — API Endpoints | 7 | | | |
| D — HTTPS | 4 | | | |
| E — Android App | 6 | | | |
| F — Restart Resilience | 3 | | | |
| G — CI/CD | 4 | | | |
| **Total** | **36** | | | |

---

## Go/No-Go Decision

| Condition | Decision |
|-----------|----------|
| All A + B + C items ✅ | ✅ **GO** — backend deploy successful |
| All E1–E3 ✅ | ✅ **GO** — Android app ready for demo |
| Any Critical item ❌ | ❌ **NO-GO** — fix before demo |
| D items ⬜ (HTTPS not set up yet) | ⚠️ **OK for internal demo**, required before public release |

---

*Last updated: May 2026 — Emotion Friend v1.0 MVP*
