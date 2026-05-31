# Deploy Emotion Friend Len Ubuntu Bang Docker Hub

Tai lieu nay dung luong don gian nhat:
1. Build image tren local
2. Push image len Docker Hub
3. Tren server chi pull image ve va chay

Khong dung HTTPS trong guide nay. Chi HTTP cong 80.

## 1. Cau truc server gon de theo doi

Toan bo file deploy dat truc tiep tai:

```bash
/opt/emotion-friend/
```

Ben trong chi giu cac file sau:

```bash
/opt/emotion-friend/.env
/opt/emotion-friend/docker-compose.yml
/opt/emotion-friend/nginx.conf
/opt/emotion-friend/res/img
```

Khong dung duong dan long `deploy/ubuntu-http` nua.

## 2. Neu dang o folder cu, migrate ve root ngay

Chay tren VPS:

```bash
cd /opt/emotion-friend

cp deploy/ubuntu-http/docker-compose.yml ./docker-compose.yml
cp deploy/ubuntu-http/nginx.conf ./nginx.conf
cp deploy/ubuntu-http/.env ./.env

mkdir -p ./res
cp -r deploy/ubuntu-http/res/img ./res/

# Kiem tra
ls -la /opt/emotion-friend
ls -la /opt/emotion-friend/res/img
```

Sau khi chay on dinh, ban co the xoa folder cu:

```bash
rm -rf /opt/emotion-friend/deploy
```

## 3. Local: build va push image len Docker Hub

Tu thu muc goc project tren local:

```bash
docker login -u trungnghia2703

docker build -t trungnghia2703/emotion-friend:backend-latest -f backend-api/Dockerfile backend-api
docker build -t trungnghia2703/emotion-friend:admin-web-latest -f admin-web/Dockerfile admin-web

docker push trungnghia2703/emotion-friend:backend-latest
docker push trungnghia2703/emotion-friend:admin-web-latest
```

Neu can rollback nhanh, tao them tag theo commit:

```bash
docker tag trungnghia2703/emotion-friend:backend-latest trungnghia2703/emotion-friend:backend-<gitsha>
docker tag trungnghia2703/emotion-friend:admin-web-latest trungnghia2703/emotion-friend:admin-web-<gitsha>

docker push trungnghia2703/emotion-friend:backend-<gitsha>
docker push trungnghia2703/emotion-friend:admin-web-<gitsha>
```

## 4. Server: pull image va chay

### 4.1. Tao file .env tai /opt/emotion-friend/.env

Noi dung toi thieu:

```env
MYSQL_ROOT_PASSWORD=doi_mat_khau_manh
MYSQL_DATABASE=emotion_friend
ADMIN_TOKEN=emotion-friend

BACKEND_IMAGE=trungnghia2703/emotion-friend:backend-latest
ADMIN_WEB_IMAGE=trungnghia2703/emotion-friend:admin-web-latest
```

### 4.2. Chay stack

```bash
cd /opt/emotion-friend
docker compose --env-file .env pull
docker compose --env-file .env up -d
```

### 4.3. Kiem tra

```bash
docker compose ps
curl -sf http://localhost/health && echo OK
curl -sf http://localhost/api/topics >/dev/null && echo API_OK
curl -sf http://localhost/admin/scenarios -H "Authorization: Bearer emotion-friend" >/dev/null && echo ADMIN_OK
```

Neu admin-web bi unhealthy do healthcheck cu, sua `docker-compose.yml` nhu sau:

```yaml
healthcheck:
  test: ["CMD-SHELL", "test -s /usr/share/nginx/html/index.html || exit 1"]
```

Sau do recreate:

```bash
docker compose --env-file .env up -d --force-recreate admin-web nginx
```

## 5. Luong update moi ngay

Moi lan update backend hoac admin-web:

1. Local build image
2. Local push image
3. Tren server:

```bash
cd /opt/emotion-friend
docker compose --env-file .env pull
docker compose --env-file .env up -d
docker image prune -f
```

## 6. Android APK moi ban cap nhat

Khi code Android thay doi, build lai APK:

```bash
cd android-app
.\gradlew.bat assembleDebug
.\gradlew.bat assembleRelease
```

Vi tri file APK:

- Debug: `android-app/app/build/outputs/apk/debug/app-debug.apk`
- Release: `android-app/app/build/outputs/apk/release/app-release.apk`

## 7. Ghi chu quan trong

- Backend phuc vu anh story tu `STATIC_FILES_PATH=/app/static`, vi vay phai co `res/img` tren server.
- MySQL chi noi bo network Docker, khong mo cong public.
- Da lo Docker token trong chat thi phai rotate token ngay sau khi deploy xong.
