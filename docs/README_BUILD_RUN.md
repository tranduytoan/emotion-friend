# Hướng dẫn chạy toàn bộ hệ thống (DB + Backend + Frontend + Android)

Tài liệu này hướng dẫn chạy end-to-end project Emotion Friend trên máy local:
- MySQL + Backend + Nginx bằng Docker Compose
- Admin Web (React/Vite)
- Cài app Android lên điện thoại thật

## 1. Yêu cầu môi trường

- Windows 10/11 (khuyến nghị), có PowerShell
- Git
- Docker Desktop (đã bật Docker Compose v2)
- Node.js 20+ và npm
- Java JDK 17+
- Android Studio (khuyến nghị) hoặc Android SDK + platform-tools
- Điện thoại Android đã bật USB Debugging

## 2. Chuẩn bị biến môi trường

### 2.1. Tạo file .env ở thư mục gốc repo

Tại thư mục gốc `emotion-friend`:

```powershell
copy .env.example .env
```

docker exec -it emotion_friend_mysql mysql -u root -p emotion_friend

Mở file `.env`, kiểm tra tối thiểu các biến sau:

```env
MYSQL_ROOT_PASSWORD=Trungnghia2703
MYSQL_DATABASE=emotion_friend
ADMIN_TOKEN=emotion-admin-2703

# Android emulator:
BACKEND_URL=http://10.0.2.2:80

# Nếu chạy trên điện thoại thật, đổi BACKEND_URL thành IP LAN của máy:
# BACKEND_URL=http://192.168.x.x:80
```

Lưu ý:
- `BACKEND_URL` được Android đọc từ file `android-app/.env` khi build app.
- Nếu bạn dùng điện thoại thật, phải đổi sang IP LAN của máy tính (không dùng `10.0.2.2`).

### 2.2. Tạo file android-app/.env

```powershell
cd android-app
copy ..\.env .env
cd ..
```

## 3. Chạy Docker: MySQL + Backend + Nginx

Tại thư mục gốc `emotion-friend`:

```powershell
docker compose down
docker compose up -d --build
```

Kiểm tra container:

```powershell
docker ps
```

Bạn cần thấy các service:
- `emotion_friend_mysql`
- `emotion_friend_backend`
- `emotion_friend_nginx`

## 4. Verify backend sau khi up Docker

Chạy nhanh các API chính:

```powershell
Invoke-RestMethod http://localhost/health
Invoke-RestMethod http://localhost/api/topics
Invoke-RestMethod http://localhost/api/scenarios
Invoke-RestMethod http://localhost/api/stories
```

Kỳ vọng hiện tại:
- `/api/topics` trả về 3 topic
- `/api/scenarios` trả về 24 câu hỏi
- `/api/stories` trả về 5 câu chuyện

Nếu cần xem log backend:

```powershell
docker logs emotion_friend_backend --tail 200
```

## 5. Chạy Admin Web frontend

Mở terminal mới:

```powershell
cd admin-web
npm install
npm run dev
```

Mặc định Vite chạy ở:
- http://localhost:3000

Admin web đã proxy:
- `/api` -> `http://localhost:8080`
- `/admin` -> `http://localhost:8080`

Trong kiến trúc đầy đủ với Nginx (port 80), bạn vẫn có thể dùng Admin Web bình thường qua proxy dev của Vite.

## 6. Chạy app Android trên điện thoại thật

### 6.1. Bật USB Debugging

Trên điện thoại:
1. Vào Cài đặt -> Giới thiệu điện thoại
2. Nhấn nhiều lần vào Số bản dựng (Build number) để mở Developer Options
3. Bật USB Debugging

### 6.2. Kiểm tra adb nhận thiết bị

```powershell
adb devices
```

Thiết bị phải ở trạng thái `device`.

### 6.3. Build và cài app

```powershell
cd android-app
./gradlew.bat :app:assembleDebug
./gradlew.bat :app:installDebug
```

APK debug nằm tại:
- `android-app/app/build/outputs/apk/debug/app-debug.apk`

Nếu muốn cài lại thủ công bằng adb:

```powershell
adb install -r .\app\build\outputs\apk\debug\app-debug.apk
```

## 7. Quy trình chạy nhanh mỗi lần dev

Từ thư mục gốc:

```powershell
docker compose up -d
```

Admin web:

```powershell
cd admin-web
npm run dev
```

Android:

```powershell
cd android-app
./gradlew.bat :app:installDebug
```

## 8. Sự cố thường gặp

### 8.1. Backend không healthy

- Xem log:

```powershell
docker logs emotion_friend_backend --tail 200
```

- Nếu lỗi migration/Flyway do DB cũ, reset volume:

```powershell
docker compose down -v
docker compose up -d --build
```

### 8.2. Điện thoại không gọi được backend

- Kiểm tra `android-app/.env` đã set đúng `BACKEND_URL=http://<LAN_IP>:80`
- Điện thoại và máy tính phải cùng mạng LAN
- Tắt VPN/proxy nếu có
- Mở lại app sau khi cài lại bản debug mới

### 8.3. Admin web không login được

- Kiểm tra `ADMIN_TOKEN` trong `.env`
- Dùng đúng token khi login tại admin-web

### 8.4. adb không nhận thiết bị

- Đổi cáp USB hoặc cổng USB
- Chọn chế độ File Transfer trên điện thoại
- Xác nhận popup trust USB debugging trên điện thoại

## 9. Lệnh dừng toàn bộ

```powershell
docker compose down
```

Nếu muốn xóa cả dữ liệu MySQL local:

```powershell
docker compose down -v
```

---

Tài liệu liên quan:
- `docs/BACKEND_SETUP.md`
- `docs/LOCAL_ENVIRONMENT.md`
- `docs/BUILD_AND_RELEASE.md`

