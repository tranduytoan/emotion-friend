# Emotion Friend

> Ứng dụng di động hỗ trợ trẻ em tự kỷ học nhận biết, hiểu, biểu đạt và điều tiết cảm xúc thông qua phương pháp học dựa trên trò chơi (Game-based Learning).

[![Android](https://img.shields.io/badge/Platform-Android%208.0%2B-green?logo=android)](android-app/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue?logo=kotlin)](android-app/)
[![Ktor](https://img.shields.io/badge/Backend-Ktor-orange?logo=kotlin)](backend-api/)
[![MySQL](https://img.shields.io/badge/Database-MySQL%208-blue?logo=mysql)](infra/)
[![License](https://img.shields.io/badge/License-Academic-lightgrey)](#)

---

## Mục lục

- [Tổng quan](#tổng-quan)
- [Đối tượng người dùng](#đối-tượng-người-dùng)
- [Tính năng chính](#tính-năng-chính)
- [Tech Stack](#tech-stack)
- [Cấu trúc repository](#cấu-trúc-repository)
- [Yêu cầu cài đặt](#yêu-cầu-cài-đặt)
- [Hướng dẫn chạy Android App](#hướng-dẫn-chạy-android-app)
- [Hướng dẫn chạy Backend + MySQL](#hướng-dẫn-chạy-backend--mysql)
- [Quy trình phát triển](#quy-trình-phát-triển)
- [Quy ước commit](#quy-ước-commit)
- [Phạm vi demo P6](#phạm-vi-demo-p6)

---

## Tổng quan

**Emotion Friend** là ứng dụng Android dành cho trẻ em mắc rối loạn phổ tự kỷ (ASD) trong độ tuổi 4–10. Ứng dụng sử dụng hình ảnh trực quan, hoạt ảnh nhẹ nhàng và cơ chế phản hồi tức thì để xây dựng năng lực cảm xúc — một kỹ năng thiết yếu nhưng thường bị khiếm khuyết ở trẻ tự kỷ.

Toàn bộ dữ liệu người dùng được lưu **cục bộ trên thiết bị** (offline-first); backend API được thiết kế để mở rộng trong tương lai.

> Xem chi tiết phạm vi sản phẩm tại [docs/PROJECT_SCOPE.md](docs/PROJECT_SCOPE.md).

---

## Đối tượng người dùng

| Nhóm | Mô tả |
|---|---|
| **Người dùng chính** | Trẻ em tự kỷ, 4–10 tuổi — tương tác trực tiếp với ứng dụng |
| **Người dùng phụ** | Phụ huynh và giáo viên — theo dõi tiến trình, hướng dẫn trẻ sử dụng |

---

## Tính năng chính

| Module | Mô tả |
|---|---|
| **Learn Emotion** | Học nhận biết cảm xúc qua flashcard + quiz lựa chọn hình ảnh |
| **Situation** | Xem tình huống xã hội, chọn cảm xúc phù hợp, nhận giải thích |
| **Camera Practice** | Luyện biểu đạt cảm xúc qua camera (CameraX preview + mock feedback) |
| **Relax** | Kỹ thuật thở có hướng dẫn hoạt ảnh giúp trẻ bình tĩnh |
| **Personal Emotion / Journal** | Ghi nhận cảm xúc hằng ngày, lưu cục bộ bằng Room |
| **Progress** | Xem lại lịch sử cảm xúc dưới dạng biểu đồ / danh sách |

---

## Tech Stack

### Android App (`android-app/`)

| Thành phần | Công nghệ |
|---|---|
| Ngôn ngữ | Kotlin |
| UI Framework | Jetpack Compose + Material 3 |
| Điều hướng | Navigation Compose |
| Dependency Injection | Hilt |
| Quản lý trạng thái | ViewModel + StateFlow |
| Lưu trữ cục bộ | Room (SQLite) |
| Lưu cài đặt | DataStore Preferences |
| Camera | CameraX |
| Build system | Gradle (Kotlin DSL) |
| Min SDK | API 26 (Android 8.0) |
| Target SDK | API 35 |

### Backend API (`backend-api/`)

| Thành phần | Công nghệ |
|---|---|
| Ngôn ngữ | Kotlin |
| Framework | Ktor |
| Database | MySQL 8 |
| ORM / Query DSL | Exposed |
| Connection Pool | HikariCP |
| Migration | Flyway |
| Build system | Gradle (Kotlin DSL) |

### Infrastructure (`infra/`)

| Thành phần | Công nghệ |
|---|---|
| Containerisation | Docker + Docker Compose |
| Database | MySQL 8 (container) |

---

## Cấu trúc repository

```
emotion-friend/
│
├── android-app/                  # Ứng dụng Android native
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/emotionfriend/
│   │   │   │   ├── ui/           # Composable screens & components
│   │   │   │   ├── viewmodel/    # ViewModels
│   │   │   │   ├── data/         # Repositories, Room DAOs, DataStore
│   │   │   │   ├── domain/       # Use cases, models
│   │   │   │   └── di/           # Hilt modules
│   │   │   └── res/              # Drawables, strings, themes
│   │   └── build.gradle.kts
│   └── build.gradle.kts
│
├── backend-api/                  # Ktor REST API (skeleton)
│   ├── src/main/kotlin/
│   │   ├── Application.kt
│   │   ├── plugins/              # Routing, serialization, CORS
│   │   └── routes/               # API route handlers
│   ├── src/main/resources/
│   │   ├── application.conf
│   │   └── db/migration/         # Flyway SQL scripts
│   └── build.gradle.kts
│
├── infra/
│   ├── docker-compose.yml        # MySQL + backend containers
│   └── .env.example              # Biến môi trường mẫu
│
├── docs/
│   └── PROJECT_SCOPE.md          # Phạm vi sản phẩm, acceptance criteria
│
├── .gitignore
├── .editorconfig
└── README.md                     # Tài liệu này
```

---

## Yêu cầu cài đặt

### Chung

| Công cụ | Phiên bản khuyến nghị | Ghi chú |
|---|---|---|
| Git | ≥ 2.40 | |
| JDK | 17 (Temurin / Corretto) | Cần cho cả Android và backend |

### Android App

| Công cụ | Phiên bản |
|---|---|
| Android Studio | Ladybug (2024.2.x) trở lên |
| Android SDK | API 26–35 |
| Gradle | 8.x (wrapper tự tải) |

### Backend + Infrastructure

| Công cụ | Phiên bản |
|---|---|
| Docker Desktop | ≥ 4.20 |
| Docker Compose | v2 (đi kèm Docker Desktop) |

> **Lưu ý:** Không cần cài MySQL thủ công. MySQL chạy trong Docker container được quản lý bởi `infra/docker-compose.yml`.

---

## Hướng dẫn chạy Android App

### 1. Clone repository

```bash
git clone <repository-url>
cd emotion-friend
```

### 2. Mở project trong Android Studio

```
File → Open → chọn thư mục android-app/
```

Chờ Gradle sync hoàn thành (lần đầu cần tải dependencies, mất vài phút).

### 3. Chạy trên thiết bị / emulator

**Thiết bị thật (khuyến nghị cho demo):**
1. Bật **Developer Options** và **USB Debugging** trên điện thoại
2. Kết nối USB → Android Studio nhận diện thiết bị
3. Nhấn **Run ▶** (Shift+F10)

**Emulator:**
1. Tạo AVD trong `Device Manager` (API 26+, x86_64)
2. Khởi động AVD → Nhấn **Run ▶**

### 4. Build APK (tuỳ chọn)

```bash
cd android-app
./gradlew assembleDebug
# APK xuất ra: app/build/outputs/apk/debug/app-debug.apk
```

### 5. Kiểm tra code tĩnh với detekt

```bash
cd android-app
./gradlew detekt
# Báo cáo HTML: app/build/reports/detekt/detekt.html
# Báo cáo XML : app/build/reports/detekt/detekt.xml
```

> **Lưu ý:** Trong môi trường phát triển, build **không thất bại** khi có cảnh báo từ detekt (`ignoreFailures = true`).  
> Báo cáo vẫn được tạo để tham khảo. Cấu hình nằm tại `android-app/config/detekt/detekt.yml`.

---

## Hướng dẫn chạy Backend + MySQL

### 1. Cấu hình biến môi trường

```bash
cp infra/.env.example infra/.env
# Chỉnh sửa infra/.env nếu cần (port, password, ...)
```

### 2. Khởi động containers

```bash
cd infra
docker compose up -d
```

Lệnh này sẽ:
- Khởi động MySQL 8 trên port `3306`
- Khởi động Ktor backend trên port `8080`
- Flyway tự động chạy migration khi backend start

### 3. Kiểm tra backend hoạt động

```bash
curl http://localhost:8080/api/health
# Kỳ vọng: 200 OK
```

### 4. Xem logs

```bash
docker compose logs -f backend
docker compose logs -f mysql
```

### 5. Dừng containers

```bash
docker compose down
# Xoá cả volume (reset database):
docker compose down -v
```

### Chạy backend không dùng Docker (tuỳ chọn)

```bash
cd backend-api
# Đảm bảo MySQL đang chạy và đã cấu hình src/main/resources/application.conf
./gradlew run
```

---

## Quy trình phát triển

### Mô hình nhánh (Branch Strategy)

```
main                  ← nhánh ổn định, chỉ merge qua PR đã review
└── develop           ← nhánh tích hợp, base cho feature branches
    ├── feature/scr-home
    ├── feature/flow-learn-emotion
    ├── feature/flow-situation
    ├── feature/flow-journal-progress
    ├── feature/camera-practice
    ├── feature/relax-screen
    └── fix/room-migration-v2
```

| Nhánh | Mục đích | Merge vào |
|---|---|---|
| `main` | Code đã demo / nộp | — |
| `develop` | Tích hợp các tính năng | `main` (qua PR) |
| `feature/<tên>` | Phát triển 1 tính năng / màn hình | `develop` |
| `fix/<tên>` | Sửa lỗi | `develop` (hoặc `main` nếu hotfix) |

### Quy tắc Pull Request

1. **Không push thẳng lên `main` hoặc `develop`** — mọi thay đổi phải qua PR
2. Mỗi PR phải có ít nhất **1 người review** trước khi merge
3. PR title theo định dạng commit convention (xem bên dưới)
4. Gắn label phù hợp: `feature`, `fix`, `docs`, `chore`
5. Đóng issue liên quan bằng `Closes #<issue-number>` trong PR description

---

## Quy ước commit

Dự án tuân theo **Conventional Commits** ([conventionalcommits.org](https://www.conventionalcommits.org)):

```
<type>(<scope>): <mô tả ngắn>

[body tuỳ chọn — giải thích WHY, không phải WHAT]

[footer tuỳ chọn — BREAKING CHANGE, Closes #issue]
```

### Các type hợp lệ

| Type | Khi nào dùng |
|---|---|
| `feat` | Thêm tính năng mới |
| `fix` | Sửa lỗi |
| `docs` | Thay đổi tài liệu |
| `style` | Format code, không thay đổi logic |
| `refactor` | Tái cấu trúc code, không thêm tính năng / sửa lỗi |
| `test` | Thêm hoặc sửa test |
| `chore` | Cập nhật dependencies, cấu hình build |
| `ci` | Thay đổi CI/CD pipeline |

### Ví dụ commit hợp lệ

```bash
feat(learn-emotion): add flashcard screen with answer selection
fix(room): add migration from version 1 to 2 for emotion_log table
docs: add project README
chore(deps): upgrade compose bom to 2024.09.00
refactor(journal): extract EmotionGrid into reusable component
```

### Quy tắc bổ sung

- Dùng tiếng Anh cho commit message (nhất quán với code)
- Mô tả ngắn **không** viết hoa chữ đầu, **không** kết thúc bằng dấu chấm
- Độ dài mô tả ngắn tối đa 72 ký tự

---

## Phạm vi demo P6

Bản demo P6 phải bao gồm đủ **7 màn hình** và **3 luồng runnable** sau:

### Màn hình bắt buộc

| # | Màn hình | Mô tả |
|---|---|---|
| 1 | Home | Điều hướng trung tâm |
| 2 | Learn Emotion | Flashcard + quiz cảm xúc |
| 3 | Situation | Tình huống xã hội + giải thích |
| 4 | Camera Practice | CameraX preview hoặc mock UI |
| 5 | Relax | Hướng dẫn thở có hoạt ảnh |
| 6 | Personal Emotion / Journal | Ghi nhận cảm xúc hằng ngày |
| 7 | Progress | Lịch sử cảm xúc |

### Luồng runnable bắt buộc

| Luồng | Mô tả |
|---|---|
| **FLOW-01** | Xem flashcard → chọn đáp án → nhận phản hồi đúng/sai |
| **FLOW-02** | Xem tình huống xã hội → chọn cảm xúc → đọc giải thích |
| **FLOW-03** | Chọn cảm xúc hiện tại → lưu → mở Progress → xem lại dữ liệu |

### Ngoài phạm vi P6

- Hệ thống đăng nhập
- Nhận diện cảm xúc AI thật
- Push Notification
- CMS / Admin Panel
- Đồng bộ đa thiết bị

> Chi tiết đầy đủ, acceptance criteria và checklist demo: [docs/PROJECT_SCOPE.md](docs/PROJECT_SCOPE.md).

---

## Liên hệ & Đóng góp

Đây là dự án học thuật. Mọi đóng góp từ thành viên trong nhóm thực hiện qua Pull Request theo quy trình mô tả ở trên.

---

*Cập nhật lần cuối: 03/05/2026*
