# Emotion Friend — Hướng Dẫn Cài Đặt Backend

## Yêu Cầu

| Công cụ | Phiên bản tối thiểu |
|---------|---------------------|
| Docker Desktop | 24+ |
| Docker Compose | v2 (tích hợp trong Docker Desktop) |
| JDK (tuỳ chọn, chỉ cần khi chạy không dùng Docker) | 21 |

---

## Kiến Trúc

```
infra/
  docker-compose.yml   ← orchestration
backend-api/
  Dockerfile           ← multi-stage build (JDK 21 → JRE 21)
  src/...              ← Ktor source
```

Hai service chạy cùng nhau:
- **mysql** — MySQL 8.4, tự động tạo database `emotion_friend`
- **backend-api** — Ktor API, chờ MySQL healthy rồi mới khởi động

---

## Cài Đặt Nhanh

### 1. Chuẩn Bị File Môi Trường

```bash
cd infra
cp .env.example .env
# Tuỳ chỉnh mật khẩu trong .env nếu muốn
```

### 2. Khởi Động Dịch Vụ

```bash
cd infra
docker compose up -d
```

Docker sẽ:
1. Pull image `mysql:8.4`
2. Build image `backend-api` từ `Dockerfile`
3. Khởi động MySQL, chờ healthy check (~30 giây)
4. Khởi động backend-api, chạy Flyway migration

### 3. Kiểm Tra Health Endpoint

```bash
# Port 8081 = host port được map từ container port 8080 (xem docker-compose.yml)
curl http://localhost:8081/health
```

Kết quả mong đợi:
```json
{
  "success": true,
  "data": { "status": "ok", "version": "1.0.0" }
}
```

### 4. Xem Log

```bash
# Tất cả service
docker compose logs -f

# Chỉ backend
docker compose logs -f backend-api

# Chỉ MySQL
docker compose logs -f mysql
```

### 5. Dừng Dịch Vụ

```bash
docker compose down
```

---

## Reset Database (Xoá Toàn Bộ Dữ Liệu)

> ⚠️ Lệnh này **xoá vĩnh viễn** tất cả dữ liệu trong volume MySQL.

```bash
docker compose down -v
docker compose up -d
```

Flyway sẽ tự chạy lại migration từ đầu khi backend khởi động.

---

## Endpoints MVP

| Method | URL | Mô tả |
|--------|-----|-------|
| GET | `/health` | Kiểm tra server đang chạy |
| GET | `/api/emotions` | Danh sách cảm xúc |
| GET | `/api/scenarios` | Danh sách tình huống học |
| POST | `/api/journal-entries` | Thêm nhật ký cảm xúc |
| POST | `/api/practice-attempts` | Lưu kết quả luyện tập |
| GET | `/api/progress/{childId}` | Xem tiến độ của trẻ |

---

## Biến Môi Trường

| Biến | Mặc định | Mô tả |
|------|----------|-------|
| `MYSQL_ROOT_PASSWORD` | `rootpassword` | Mật khẩu root MySQL |
| `MYSQL_USER` | `efuser` | User app MySQL |
| `MYSQL_PASSWORD` | `efpassword` | Mật khẩu user app |
| `PORT` | `8080` | Port backend lắng nghe (nội bộ container) |
| `DATABASE_URL` | *(auto từ compose)* | JDBC URL kết nối MySQL |
| `DATABASE_USER` | *(= `MYSQL_USER`)* | Username backend dùng để kết nối DB |
| `DATABASE_PASSWORD` | *(= `MYSQL_PASSWORD`)* | Mật khẩu backend dùng để kết nối DB |

---

## Chạy Backend Không Dùng Docker (Dev Mode)

Cần MySQL đang chạy (hoặc dùng `docker compose up mysql -d`):

```bash
cd backend-api

# Windows PowerShell
$env:DATABASE_URL = "jdbc:mysql://localhost:3306/emotion_friend?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DATABASE_USER = "efuser"
$env:DATABASE_PASSWORD = "efpassword"
$env:JAVA_HOME = "D:\DevTools\Java\jdk-21.0.5"
.\gradlew.bat run
```

Backend khởi động tại `http://localhost:8080`.
