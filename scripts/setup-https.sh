#!/usr/bin/env bash
# =============================================================================
# setup-https.sh — Obtain Let's Encrypt certificate for Emotion Friend
# =============================================================================
# Run ONCE on the VPS after the stack is running on port 80.
#
# Usage:
#   chmod +x scripts/setup-https.sh
#   ./scripts/setup-https.sh your-domain.com your@email.com
#
# After running:
#   docker compose -f docker-compose.yml -f docker-compose.https.yml up -d --build
# =============================================================================
set -euo pipefail

DOMAIN="${1:?Usage: $0 <domain> <email>}"
EMAIL="${2:?Usage: $0 <domain> <email>}"

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"

echo "==> [1/4] Replacing domain placeholder in nginx-https.conf ..."
sed -i "s/YOUR_DOMAIN\.COM/${DOMAIN}/g" "${REPO_ROOT}/nginx/nginx-https.conf"
echo "    Done — nginx-https.conf updated for ${DOMAIN}"

echo "==> [2/4] Ensuring base stack is running on port 80 (nginx + certbot_webroot) ..."
cd "${REPO_ROOT}"
docker compose up -d nginx
# Give nginx a moment to start
sleep 3

echo "==> [3/4] Requesting Let's Encrypt certificate via webroot challenge ..."
docker run --rm \
  -v emotion-friend_letsencrypt_certs:/etc/letsencrypt \
  -v emotion-friend_certbot_webroot:/var/www/certbot \
  certbot/certbot certonly \
  --webroot \
  --webroot-path=/var/www/certbot \
  --email "${EMAIL}" \
  --agree-tos \
  --no-eff-email \
  -d "${DOMAIN}"

echo "==> [4/4] Certificate obtained. Switching to HTTPS stack ..."
docker compose -f docker-compose.yml -f docker-compose.https.yml up -d --build
echo ""
echo "✓ HTTPS setup complete!"
echo ""
echo "Verify:"
echo "  curl -I https://${DOMAIN}/health"
echo "  openssl s_client -connect ${DOMAIN}:443 -servername ${DOMAIN} < /dev/null 2>&1 | grep 'subject\\|issuer\\|Verify'"
