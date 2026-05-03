# Hướng dẫn cài đặt môi trường phát triển cục bộ

> **Emotion Friend** — Cấu hình máy tính cho thành viên nhóm  
> Cập nhật lần cuối: 03/05/2026

---

## Mục lục

1. [Yêu cầu công cụ](#1-yêu-cầu-công-cụ)
2. [Cài đặt JDK 17](#2-cài-đặt-jdk-17)
3. [Cài đặt Android Studio](#3-cài-đặt-android-studio)
4. [Cấu hình Android Emulator hoặc thiết bị thật](#4-cấu-hình-android-emulator-hoặc-thiết-bị-thật)
5. [Cài đặt Docker Desktop](#5-cài-đặt-docker-desktop)
6. [Cài đặt Git](#6-cài-đặt-git)
7. [VS Code (tuỳ chọn)](#7-vs-code-tuỳ-chọn)
8. [MySQL Client (tuỳ chọn)](#8-mysql-client-tuỳ-chọn)
9. [Cấu hình file môi trường](#9-cấu-hình-file-môi-trường)
10. [Kiểm tra môi trường](#10-kiểm-tra-môi-trường)

---

## 1. Yêu cầu công cụ

| Công cụ | Phiên bản tối thiểu | Bắt buộc | Ghi chú |
|---|---|---|---|
| **JDK 17** | 17 LTS (Temurin hoặc Corretto) | ✅ Bắt buộc | Cần cho Android build và backend Ktor |
| **Android Studio** | Ladybug (2024.2.x) | ✅ Bắt buộc | IDE chính để phát triển Android |
| **Git** | 2.40+ | ✅ Bắt buộc | Quản lý source code |
| **Docker Desktop** | 4.20+ | ✅ Bắt buộc | Chạy MySQL và backend qua container |
| **Android Emulator / Thiết bị thật** | API 26+ | ✅ Bắt buộc | Cần ít nhất một trong hai để test |
| **VS Code** | Bất kỳ | ⚪ Tuỳ chọn | Chỉnh sửa docs, config, backend nếu không dùng IntelliJ |
| **MySQL Client** | Bất kỳ | ⚪ Tuỳ chọn | Xem/truy vấn database cục bộ khi cần debug |

---

## 2. Cài đặt JDK 17

JDK 17 là bắt buộc cho cả Android build (Gradle) và backend Ktor.

### Windows / macOS / Linux — Cách nhanh nhất (SDKMAN)

```bash
# Cài SDKMAN (macOS/Linux)
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Cài Temurin 17
sdk install java 17.0.11-tem
sdk use java 17.0.11-tem
```

### Windows — Tải trực tiếp

1. Truy cập [https://adoptium.net](https://adoptium.net)
2. Chọn **Temurin 17 (LTS)** → tải bản installer `.msi`
3. Chạy installer, chọn tuỳ chọn **"Set JAVA_HOME"**

### Kiểm tra sau khi cài

```bash
java -version
# Kết quả kỳ vọng: openjdk version "17.x.x" ...

javac -version
# Kết quả kỳ vọng: javac 17.x.x
```

> **Lưu ý Android Studio:** Android Studio đi kèm JDK nội bộ riêng. Để đảm bảo Gradle dùng đúng JDK 17, vào `File → Project Structure → SDK Location → Gradle Settings → Gradle JDK` → chọn JDK 17 vừa cài.

---

## 3. Cài đặt Android Studio

### Tải về

- Truy cập [https://developer.android.com/studio](https://developer.android.com/studio)
- Tải phiên bản **Ladybug (2024.2.x)** trở lên

### Cài đặt SDK components bắt buộc

Sau khi cài Android Studio, mở **SDK Manager** (`Tools → SDK Manager`) và cài:

| Component | Phiên bản |
|---|---|
| Android SDK Platform | API 26, API 35 |
| Android SDK Build-Tools | 35.x |
| Android SDK Platform-Tools | Mới nhất |
| Android Emulator | Mới nhất |
| Google Play Intel x86_64 Atom System Image | API 26 và API 35 |

### Cài đặt plugin bổ sung (khuyến nghị)

- **Kotlin** — thường đi kèm sẵn
- **Jetpack Compose** — thường đi kèm sẵn
- **ktlint** (plugin `KtLint` hoặc `Ktlint-CLI`) — lint code Kotlin

---

## 4. Cấu hình Android Emulator hoặc thiết bị thật

### Phương án A — Android Emulator (phát triển hằng ngày)

1. Trong Android Studio, mở **Device Manager** (`Tools → Device Manager`)
2. Nhấn **Create Virtual Device**
3. Chọn cấu hình: **Pixel 6** (hoặc tương đương)
4. Chọn System Image: **API 35, x86_64, Google Play**
5. Hoàn thành wizard → nhấn **▶ Start** để khởi động

**Lưu ý hiệu năng:**
- Bật **Hardware Acceleration** (Intel HAXM hoặc Windows Hypervisor Platform)
- Cấp tối thiểu **4 GB RAM** cho AVD
- Không mở quá 1 emulator cùng lúc trên máy yếu

### Phương án B — Thiết bị Android thật (bắt buộc cho demo P6)

1. Trên điện thoại: `Cài đặt → Giới thiệu → Phiên bản bản dựng` → nhấn **7 lần** để bật Developer Options
2. Vào `Cài đặt → Tùy chọn nhà phát triển` → bật **USB Debugging**
3. Kết nối USB vào máy tính → chấp nhận prompt "Allow USB Debugging"
4. Kiểm tra thiết bị nhận diện:

```bash
adb devices
# Kết quả kỳ vọng: <serial>    device
```

---

## 5. Cài đặt Docker Desktop

Docker Desktop dùng để chạy MySQL và backend Ktor trong container, không cần cài MySQL thủ công.

### Tải về

- **Windows / macOS:** [https://www.docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop)
- **Linux:** Dùng package manager hoặc [https://docs.docker.com/engine/install](https://docs.docker.com/engine/install)

### Yêu cầu hệ thống

| OS | Yêu cầu |
|---|---|
| Windows 10/11 | WSL 2 bật sẵn, Virtualization enabled trong BIOS |
| macOS | Apple Silicon hoặc Intel, macOS 12+ |
| Linux | Kernel 5.15+, không cần Docker Desktop, dùng Docker Engine |

### Kiểm tra sau khi cài

```bash
docker --version
# Docker version 26.x.x, build ...

docker compose version
# Docker Compose version v2.x.x
```

> **Lưu ý Windows:** Đảm bảo WSL 2 đã được cài và Docker Desktop dùng WSL 2 backend (`Settings → General → Use the WSL 2 based engine`).

---

## 6. Cài đặt Git

### Windows

Tải từ [https://git-scm.com/download/win](https://git-scm.com/download/win) — chọn bản 64-bit.  
Khi cài, chọn **"Git from the command line and also from 3rd-party software"**.

### macOS

```bash
# Cài qua Homebrew
brew install git
```

### Linux

```bash
sudo apt update && sudo apt install git    # Ubuntu / Debian
sudo dnf install git                        # Fedora
```

### Cấu hình Git sau khi cài

```bash
git config --global user.name "Tên của bạn"
git config --global user.email "email@example.com"
git config --global core.autocrlf input     # macOS/Linux
git config --global core.autocrlf true      # Windows
git config --global init.defaultBranch main
```

### Kiểm tra

```bash
git --version
# git version 2.x.x
```

---

## 7. VS Code (tuỳ chọn)

VS Code hữu ích để chỉnh sửa tài liệu, file cấu hình, Docker Compose, và backend nếu không dùng IntelliJ IDEA.

### Tải về

[https://code.visualstudio.com](https://code.visualstudio.com)

### Extension khuyến nghị

Cài qua `Extensions` panel (Ctrl+Shift+X):

| Extension | Mục đích |
|---|---|
| **Kotlin** (JetBrains) | Syntax highlighting cho Kotlin |
| **Docker** (Microsoft) | Quản lý container, xem logs |
| **YAML** (Red Hat) | Hỗ trợ `.yml` / `.yaml` |
| **Markdown All in One** | Preview và format `.md` |
| **EditorConfig for VS Code** | Tôn trọng `.editorconfig` |
| **GitLens** | Xem git history, blame |

---

## 8. MySQL Client (tuỳ chọn)

MySQL Client dùng để kết nối vào MySQL container và truy vấn trực tiếp khi debug. Không cần thiết để chạy ứng dụng.

### Lựa chọn phổ biến

| Công cụ | OS | Ghi chú |
|---|---|---|
| **TablePlus** | Windows / macOS | Giao diện đẹp, miễn phí có giới hạn |
| **DBeaver Community** | Windows / macOS / Linux | Miễn phí, đầy đủ tính năng |
| **MySQL Workbench** | Windows / macOS / Linux | Chính thức từ Oracle, miễn phí |
| **mysql CLI** | Mọi OS | Dòng lệnh, nhẹ nhất |

### Kết nối tới MySQL container (sau khi `docker compose up`)

| Thông số | Giá trị |
|---|---|
| Host | `localhost` |
| Port | `3306` |
| Database | `emotion_friend` |
| User | `emotion_user` |
| Password | *(giá trị trong `mysql.env`)* |

---

## 9. Cấu hình file môi trường

File `.env` chứa thông tin nhạy cảm **không được commit vào Git**. Mỗi thành viên tự tạo file thực tế từ file mẫu `.example`.

### Bước thực hiện

```bash
# Từ thư mục gốc của project
cd infra/env

# Tạo file thực tế từ file mẫu
cp backend.env.example backend.env
cp mysql.env.example mysql.env
```

### Nội dung mặc định (cho môi trường local)

**`infra/env/backend.env`:**
```
APP_ENV=development
SERVER_PORT=8080
DB_HOST=mysql
DB_PORT=3306
DB_NAME=emotion_friend
DB_USER=emotion_user
DB_PASSWORD=emotion_password
```

**`infra/env/mysql.env`:**
```
MYSQL_DATABASE=emotion_friend
MYSQL_USER=emotion_user
MYSQL_PASSWORD=emotion_password
MYSQL_ROOT_PASSWORD=root_password
```

> ⚠️ Giá trị mặc định chỉ dùng cho **môi trường local**. Không dùng các giá trị này trên server production.

---

## 10. Kiểm tra môi trường

Sau khi cài đặt xong, chạy lần lượt các lệnh sau và đảm bảo tất cả đều thành công:

```bash
# JDK
java -version          # openjdk 17.x.x
javac -version         # javac 17.x.x

# Git
git --version          # git version 2.x.x

# Docker
docker --version       # Docker version 26.x.x
docker compose version # Docker Compose version v2.x.x

# ADB (Android Debug Bridge — đi kèm Android Studio)
adb version            # Android Debug Bridge version 1.x.x
```

**Kiểm tra Docker hoạt động:**

```bash
docker run --rm hello-world
# Kết quả kỳ vọng: "Hello from Docker!"
```

**Kiểm tra clone và build Android:**

```bash
git clone <repository-url>
cd emotion-friend/android-app
./gradlew assembleDebug
# BUILD SUCCESSFUL
```

---

## Xử lý sự cố thường gặp

| Triệu chứng | Nguyên nhân phổ biến | Cách khắc phục |
|---|---|---|
| `JAVA_HOME` không tìm thấy | Biến môi trường chưa set | Set `JAVA_HOME` trỏ đến thư mục JDK 17 |
| Gradle sync thất bại | JDK sai phiên bản | Kiểm tra `File → Project Structure → Gradle JDK` → chọn JDK 17 |
| `adb: command not found` | Platform-tools chưa trong PATH | Thêm `$ANDROID_HOME/platform-tools` vào PATH |
| Docker không start | Virtualization chưa bật | Vào BIOS bật Intel VT-x / AMD-V |
| Port 3306 đã bị dùng | MySQL local đang chạy | Dừng MySQL local: `net stop mysql` (Windows) hoặc `sudo systemctl stop mysql` |
| Port 8080 đã bị dùng | Ứng dụng khác chiếm port | Đổi `SERVER_PORT` trong `backend.env` và cập nhật `docker-compose.yml` |

---

*Nếu gặp sự cố không có trong bảng trên, tạo issue trên GitHub và tag `@nghia` hoặc đặt câu hỏi trong kênh nhóm.*
