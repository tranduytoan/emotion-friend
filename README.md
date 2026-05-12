# Emotion Friend

> Ứng dụng di động hỗ trợ trẻ em tự kỷ học nhận biết, hiểu, biểu đạt và điều tiết cảm xúc thông qua phương pháp học dựa trên trò chơi (Game-based Learning).

[![Android](https://img.shields.io/badge/Platform-Android%208.0%2B-green?logo=android)](android-app/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue?logo=kotlin)](android-app/)
[![Ktor](https://img.shields.io/badge/Backend-Ktor-orange?logo=kotlin)](backend-api/)
[![MySQL](https://img.shields.io/badge/Database-MySQL%208-blue?logo=mysql)](infra/)
[![CI](https://img.shields.io/badge/CI-GitHub%20Actions-2088FF?logo=github-actions&logoColor=white)](.github/workflows/)
[![License](https://img.shields.io/badge/License-Academic-lightgrey)](#)

---

## Mục lục

1. [Tổng quan](#tổng-quan)
2. [Tính năng đã hoàn thành](#tính-năng-đã-hoàn-thành)
3. [Giới hạn hiện tại](#giới-hạn-hiện-tại)
4. [Tech Stack](#tech-stack)
5. [Cấu trúc repository](#cấu-trúc-repository)
6. [Yêu cầu cài đặt](#yêu-cầu-cài-đặt)
7. [Hướng dẫn chạy Android App](#hướng-dẫn-chạy-android-app)
8. [Hướng dẫn build APK](#hướng-dẫn-build-apk)
9. [Hướng dẫn chạy Backend + MySQL](#hướng-dẫn-chạy-backend--mysql)
10. [Luồng demo cho giảng viên](#luồng-demo-cho-giảng-viên)
11. [Trách nhiệm kỹ thuật của nhóm](#trách-nhiệm-kỹ-thuật-của-nhóm)
12. [Tài liệu tham khảo](#tài-liệu-tham-khảo)

---

## Tổng quan

**Emotion Friend** là ứng dụng Android dành cho trẻ em mắc rối loạn phổ tự kỷ (ASD) trong độ tuổi 4–10. Ứng dụng sử dụng hình ảnh trực quan, hoạt ảnh nhẹ nhàng và cơ chế phản hồi tức thì để xây dựng năng lực cảm xúc — một kỹ năng thiết yếu nhưng thường bị khiếm khuyết ở trẻ tự kỷ.

| Thuộc tính | Chi tiết |
|---|---|
| **Nền tảng** | Android native (Kotlin / Jetpack Compose) |
| **Ngôn ngữ giao diện** | Tiếng Việt |
| **Chiến lược dữ liệu** | Offline-first — mọi dữ liệu người dùng lưu cục bộ qua Room |
| **Mốc hiện tại** | P6 — Nguyên mẫu phần mềm có thể chạy được |

---

## Tính năng đã hoàn thành

### Màn hình Android (7/7)

| # | Màn hình | Mô tả | Trạng thái |
|---|---|---|---|
| 1 | **Home** | Điều hướng trung tâm tới 6 module, hiển thị lời chào | ✅ Hoàn thành |
| 2 | **Learn Emotion** | Flashcard cảm xúc + quiz trắc nghiệm, lưu kết quả vào Room | ✅ Hoàn thành |
| 3 | **Situation** | Tình huống xã hội, chọn cảm xúc phù hợp, hiển thị giải thích | ✅ Hoàn thành |
| 4 | **Personal Emotion / Journal** | Chọn và lưu cảm xúc hiện tại theo thời gian thực | ✅ Hoàn thành |
| 5 | **Progress** | Xem tóm tắt tiến trình: số bài hoàn thành, độ chính xác, nhật ký | ✅ Hoàn thành |
| 6 | **Relax** | Kỹ thuật thở 4-7-8 có hoạt ảnh, nhạc nền, bài tập xếp màu | ✅ Hoàn thành |
| 7 | **Camera Practice** | CameraX preview + mock feedback nhận diện cảm xúc | ✅ Hoàn thành |

### Hạ tầng và chất lượng

| Hạng mục | Mô tả | Trạng thái |
|---|---|---|
| **Offline-first** | Room SQLite lưu toàn bộ dữ liệu người dùng cục bộ | ✅ |
| **Seed data** | Tự động nạp 6 flashcard cảm xúc và 5 tình huống học từ assets JSON | ✅ |
| **Backend Ktor** | REST API với 6 endpoint, fake repositories (chạy không cần DB) | ✅ |
| **Docker Compose** | MySQL 8 + backend container, health check, Flyway migration | ✅ |
| **CI/CD** | GitHub Actions: detekt → unit test → build APK (Android); test → build (backend) | ✅ |
| **Unit tests** | 10 test: `LearnEmotionViewModelTest` (5) + `SituationViewModelTest` (5) | ✅ |
| **Static analysis** | detekt 1.23.6 với cấu hình tùy chỉnh (`ignoreFailures = true`) | ✅ |

---

## Giới hạn hiện tại

| # | Giới hạn | Ghi chú |
|---|---|---|
| 1 | **Không có đăng nhập / đa hồ sơ** | MVP dùng một hồ sơ trẻ duy nhất với `childId` cố định |
| 2 | **Camera không nhận diện cảm xúc thật** | CameraX hiển thị preview, phản hồi là mock UI — AI thật ngoài phạm vi P6 |
| 3 | **Backend không kết nối Android** | App hoàn toàn offline-first; backend chạy độc lập để demo API |
| 4 | **Không có push notification** | Nhắc nhở hằng ngày ngoài phạm vi P6 |
| 5 | **Không có đồng bộ đa thiết bị** | Dữ liệu chỉ tồn tại trên thiết bị hiện tại |
| 6 | **Trò chơi xếp hình (Puzzle)** | Màn hình Relax có placeholder — trò chơi đầy đủ ngoài phạm vi P6 |
| 7 | **Chưa có tài khoản phụ huynh** | Màn hình theo dõi dành cho phụ huynh ngoài phạm vi P6 |

---

## Tech Stack

### Android App (`android-app/`)

| Thành phần | Công nghệ | Phiên bản |
|---|---|---|
| Ngôn ngữ | Kotlin | 2.0.0 |
| UI Framework | Jetpack Compose + Material 3 | BOM 2024.08.00 |
| Điều hướng | Navigation Compose | 2.7.7 |
| Dependency Injection | Hilt (KSP) | 2.51.1 |
| Quản lý trạng thái | ViewModel + StateFlow | — |
| Lưu trữ cục bộ | Room | 2.6.1 |
| Camera | CameraX | 1.3.4 |
| Network client | Ktor client | 2.3.12 |
| Background tasks | WorkManager | 2.9.1 |
| Min SDK / Target SDK | API 26 / API 35 | Android 8.0+ |

### Backend API (`backend-api/`)

| Thành phần | Công nghệ | Phiên bản |
|---|---|---|
| Framework | Ktor (Netty) | 2.3.12 |
| ORM | Exposed | 0.54.0 |
| Connection pool | HikariCP | 5.1.0 |
| Database | MySQL | 8.4.0 |
| Migration | Flyway | 9.22.3 |

---

## Cấu trúc repository

```
emotion-friend/
├── android-app/                  # Ứng dụng Android native
│   ├── app/src/main/java/com/emotionfriend/
│   │   ├── core/                 # DI, navigation, design system
│   │   ├── data/                 # Room DAOs, entities, repositories, seed
│   │   ├── domain/               # Models, interfaces
│   │   └── feature/              # Màn hình: home, learn, situation, journal,
│   │                             #           progress, relax, express
│   ├── config/detekt/            # Cấu hình static analysis
│   └── gradle/libs.versions.toml # Version catalog
│
├── backend-api/                  # Ktor REST API
│   ├── src/main/kotlin/com/emotionfriend/api/
│   │   ├── config/               # AppConfig, DatabaseConfig
│   │   ├── db/                   # DatabaseFactory (HikariCP + Flyway)
│   │   ├── plugins/              # Routing, serialization, CORS, status pages
│   │   ├── repository/           # Interfaces + db/ và fake/ implementations
│   │   ├── routes/               # health, emotions, scenarios, journal, practice, progress
│   │   └── service/              # Business logic
│   └── src/main/resources/db/migration/
│       └── V1__initial_schema.sql
│
├── infra/
│   ├── docker-compose.yml        # MySQL + backend containers
│   ├── .env.example              # Biến môi trường mẫu (commit được)
│   └── env/                      # Template riêng cho từng service
│
├── docs/                         # Tài liệu kỹ thuật
│   ├── PROJECT_SCOPE.md
│   ├── LOCAL_ENVIRONMENT.md
│   ├── BUILD_AND_RELEASE.md
│   ├── BACKEND_SETUP.md
│   ├── DEVELOPMENT_WORKFLOW.md
│   └── QA_CHECKLIST.md
│
├── .github/workflows/
│   ├── android-ci.yml            # detekt → test → assembleDebug
│   └── backend-ci.yml            # test → build
└── README.md
```

---

## Yêu cầu cài đặt

| Công cụ | Phiên bản | Bắt buộc |
|---|---|---|
| Git | ≥ 2.40 | ✅ |
| JDK | 17 LTS (Temurin / Corretto) | ✅ |
| Android Studio | Ladybug (2024.2.x) trở lên | ✅ (chạy app) |
| Android SDK | API 26–35 | ✅ (chạy app) |
| Docker Desktop | ≥ 4.20 (Docker Compose v2) | ✅ (chạy backend) |

---

## Hướng dẫn chạy Android App

### 1. Clone repository

```bash
git clone https://github.com/nguyentrungnghia1802/emotion-friend.git
cd emotion-friend
```

### 2. Mở project trong Android Studio

```
File → Open → chọn thư mục android-app/
```

Chờ Gradle sync hoàn thành (lần đầu mất vài phút do tải dependencies).

### 3. Chạy trên thiết bị / emulator

**Thiết bị thật (khuyến nghị cho demo):**
1. Bật **Developer Options** → **USB Debugging** trên điện thoại
2. Kết nối USB — Android Studio sẽ nhận diện thiết bị tự động
3. Nhấn **Run ▶** (Shift+F10)

**Emulator:**
1. Mở **Device Manager** → tạo AVD (API 26+, x86\_64)
2. Khởi động AVD → Nhấn **Run ▶**

> App hoạt động **hoàn toàn offline** — không cần backend hay internet để demo.

---

## Hướng dẫn build APK

```powershell
# Windows PowerShell
cd android-app
$env:JAVA_HOME = "D:\DevTools\Java\jdk-21.0.5"   # điều chỉnh theo máy
.\gradlew.bat assembleDebug
```

```bash
# macOS / Linux
cd android-app
./gradlew assembleDebug
```

APK xuất ra: `android-app/app/build/outputs/apk/debug/app-debug.apk`

> Xem chi tiết quy trình build và upload: [docs/BUILD_AND_RELEASE.md](docs/BUILD_AND_RELEASE.md)

---

## Hướng dẫn chạy Backend + MySQL

Backend chạy độc lập — không phụ thuộc Android app.

### 1. Chuẩn bị biến môi trường

```bash
cd infra
cp .env.example .env
# Chỉnh mật khẩu trong .env nếu muốn (tuỳ chọn)
```

### 2. Khởi động containers

```bash
cd infra
docker compose up -d
```

Docker sẽ tự động:
- Pull `mysql:8.4`, tạo database `emotion_friend`
- Build image backend từ `Dockerfile`
- Khởi động MySQL (chờ healthy ~30 giây)
- Khởi động backend, chạy Flyway migration

| Service | Host port | Mô tả |
|---|---|---|
| MySQL | `localhost:3307` | Kết nối bằng MySQL client nếu cần debug |
| Backend API | `localhost:8081` | REST API chính |

### 3. Kiểm tra backend hoạt động

```bash
curl http://localhost:8081/health
```

Kết quả mong đợi:

```json
{
  "success": true,
  "data": { "status": "ok", "version": "1.0.0" }
}
```

### 4. Các endpoint MVP

| Method | URL | Mô tả |
|---|---|---|
| GET | `/health` | Kiểm tra server đang chạy |
| GET | `/api/emotions` | Danh sách 6 cảm xúc |
| GET | `/api/scenarios` | Danh sách tình huống học |
| POST | `/api/journal-entries` | Lưu nhật ký cảm xúc |
| POST | `/api/practice-attempts` | Lưu kết quả luyện tập |
| GET | `/api/progress/{childId}` | Tóm tắt tiến trình |

### 5. Dừng containers

```bash
docker compose down          # giữ dữ liệu
docker compose down -v       # xoá cả volume (reset DB)
```

> Xem hướng dẫn đầy đủ và biến môi trường: [docs/BACKEND_SETUP.md](docs/BACKEND_SETUP.md)

---

## Luồng demo cho giảng viên

> Mục tiêu: demo toàn bộ tính năng P6 trong ~5 phút. App không cần internet, chạy hoàn toàn offline.

### Bước 1 — Mở ứng dụng

- Màn hình **Home** hiển thị lời chào và 6 thẻ module
- Giao diện màu pastel nhẹ nhàng, font chữ lớn, icon rõ ràng

### Bước 2 — Chạy Learn Emotion

1. Nhấn **"Học cảm xúc"** trên Home
2. Flashcard hiện ra: emoji + tên cảm xúc + mô tả ngắn
3. 4 nút lựa chọn xuất hiện — chọn đáp án
4. Banner phản hồi xanh (đúng) hoặc đỏ (sai) hiện ngay lập tức
5. Nhấn **"Tiếp theo"** để sang câu hỏi mới
6. Nhấn nút back để quay về Home

### Bước 3 — Chạy Situation

1. Nhấn **"Tình huống"** trên Home
2. Thẻ tình huống hiển thị mô tả ngữ cảnh xã hội (VD: "Bạn bị vấp ngã...")
3. 4 nút cảm xúc để lựa chọn
4. Chọn sai → hiện thông báo + giải thích tại sao cảm xúc đó phù hợp
5. Chọn đúng → phản hồi tích cực + tiến sang tình huống kế tiếp

### Bước 4 — Lưu cảm xúc cá nhân (Journal)

1. Nhấn **"Cảm xúc của tôi"** trên Home
2. Chọn một cảm xúc trong danh sách (Vui / Buồn / Tức / ...)
3. Nhập ghi chú tuỳ chọn
4. Nhấn **"Lưu"** — entry được lưu vào Room ngay lập tức

### Bước 5 — Kiểm tra Progress

1. Nhấn **"Tiến trình"** trên Home
2. Màn hình hiển thị:
   - Số bài đã hoàn thành
   - Tỷ lệ đúng (accuracy rate)
   - Cảm xúc hay bị nhầm nhất
   - Số nhật ký đã ghi
3. Dữ liệu vừa ghi ở bước 4 xuất hiện trong phần nhật ký

### Bước 6 — Mở Relax

1. Nhấn **"Thư giãn"** trên Home
2. 3 hoạt động hiển thị: Nhạc thư giãn, Bài thở, Xếp hình
3. Nhấn **"Bài thở"** → hoạt ảnh vòng tròn co giãn hướng dẫn thở 4-7-8
4. Demo animation đang chạy
5. Back về Home

### Bước 7 — Mở Camera Practice

1. Nhấn **"Luyện tập camera"** trên Home
2. Màn hình yêu cầu quyền camera (lần đầu)
3. Preview camera bật lên — trẻ có thể nhìn mặt mình
4. Nhấn **"Chụp"** → mock feedback hiện nhãn cảm xúc phát hiện
5. Back về Home

---

## Trách nhiệm kỹ thuật của nhóm

| Thành viên | Vai trò | Phạm vi kỹ thuật |
|---|---|---|
| Nguyễn Trung Nghĩa | Android Lead | Kiến trúc app (Hilt, Room, Navigation), LearnScreen, SituationScreen, CI/CD |
| *(Thành viên 2)* | Android Dev | HomeScreen, JournalScreen, ProgressScreen, data layer (repositories, mappers) |
| *(Thành viên 3)* | Android Dev | RelaxScreen, ExpressCameraScreen, CameraX integration, design system |
| *(Thành viên 4)* | Backend Dev | Ktor API, Flyway migrations, Docker Compose, fake/db repositories |
| *(Thành viên 5)* | QA / Docs | Unit tests, detekt config, QA checklist, tài liệu kỹ thuật |

> Cập nhật tên thành viên thực tế trước khi nộp báo cáo.

---

## Tài liệu tham khảo

| Tài liệu | Mô tả |
|---|---|
| [docs/PROJECT_SCOPE.md](docs/PROJECT_SCOPE.md) | Phạm vi sản phẩm P6, acceptance criteria, danh sách kiểm tra demo |
| [docs/LOCAL_ENVIRONMENT.md](docs/LOCAL_ENVIRONMENT.md) | Hướng dẫn cài đặt môi trường phát triển cho thành viên mới |
| [docs/BUILD_AND_RELEASE.md](docs/BUILD_AND_RELEASE.md) | Quy trình build APK, tải artifact từ CI, cài lên thiết bị |
| [docs/QA_CHECKLIST.md](docs/QA_CHECKLIST.md) | 77 mục kiểm thử thủ công trải dài 12 kịch bản |
| [docs/BACKEND_SETUP.md](docs/BACKEND_SETUP.md) | Docker Compose, biến môi trường, endpoint API |
| [docs/DEVELOPMENT_WORKFLOW.md](docs/DEVELOPMENT_WORKFLOW.md) | Git flow, quy ước commit, quy trình PR |

---

*Cập nhật lần cuối: 12/05/2026 — Phiên bản P6*

