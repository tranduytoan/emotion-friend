# =============================================================================
# Emotion Friend — Makefile helpers
# =============================================================================
# Usage examples:
#   make up          — start all services (requires .env)
#   make down        — stop all services
#   make logs        — tail all container logs
#   make logs-back   — tail backend logs only
#   make backup-db   — dump MySQL to backups/
#   make shell-db    — open MySQL shell
#   make build       — rebuild backend image without cache
# =============================================================================

.PHONY: up down restart logs logs-back build backup-db shell-db ps

# ── Start / stop ──────────────────────────────────────────────────────────────

up:
	docker compose --env-file .env up -d --build

down:
	docker compose down --remove-orphans

restart:
	docker compose restart backend

# ── Logs ──────────────────────────────────────────────────────────────────────

logs:
	docker compose logs -f --tail=100

logs-back:
	docker compose logs -f --tail=100 backend

# ── Build ─────────────────────────────────────────────────────────────────────

build:
	docker compose build --no-cache backend

# ── Status ────────────────────────────────────────────────────────────────────

ps:
	docker compose ps

# ── Database backup ───────────────────────────────────────────────────────────

backup-db:
	@mkdir -p backups
	@TIMESTAMP=$$(date +%Y%m%d_%H%M%S) && \
	docker exec emotion_friend_mysql \
	  mysqldump -u root -p$${MYSQL_ROOT_PASSWORD} emotion_friend \
	  > backups/emotion_friend_$${TIMESTAMP}.sql && \
	echo "Backup saved: backups/emotion_friend_$${TIMESTAMP}.sql"

# ── MySQL shell ───────────────────────────────────────────────────────────────

shell-db:
	docker exec -it emotion_friend_mysql mysql -u root -p emotion_friend
