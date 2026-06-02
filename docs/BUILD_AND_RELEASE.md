# Hướng dẫn Build & Phát hành APK — Emotion Friend

> Tài liệu này dành cho người mới bắt đầu. Bạn không cần kinh nghiệm DevOps để làm theo.

---

## Mục lục

- [Hướng dẫn Build \& Phát hành APK — Emotion Friend](#hướng-dẫn-build--phát-hành-apk--emotion-friend)
  - [Mục lục](#mục-lục)
  - [1. Build APK trên máy tính cá nhân](#1-build-apk-trên-máy-tính-cá-nhân)
    - [Yêu cầu](#yêu-cầu)
    - [Các bước thực hiện](#các-bước-thực-hiện)
  - [2. Tìm file APK sau khi build](#2-tìm-file-apk-sau-khi-build)
  - [3. GitHub Actions tự động build và lưu APK](#3-github-actions-tự-động-build-và-lưu-apk)
    - [Cách tải APK từ GitHub Actions](#cách-tải-apk-từ-github-actions)
  - [4. Cài APK lên thiết bị Android](#4-cài-apk-lên-thiết-bị-android)
    - [Cách 1 — Cài qua cáp USB (khuyến nghị)](#cách-1--cài-qua-cáp-usb-khuyến-nghị)
    - [Cách 2 — Cài qua file (không cần máy tính)](#cách-2--cài-qua-file-không-cần-máy-tính)
  - [5. Danh sách kiểm tra trước buổi demo](#5-danh-sách-kiểm-tra-trước-buổi-demo)
    - [Kiểm tra code](#kiểm-tra-code)
    - [Kiểm tra APK](#kiểm-tra-apk)
    - [Kiểm tra tính năng chính](#kiểm-tra-tính-năng-chính)
    - [Kiểm tra thiết bị demo](#kiểm-tra-thiết-bị-demo)
  - [6. Kế hoạch nâng cấp: Firebase App Distribution](#6-kế-hoạch-nâng-cấp-firebase-app-distribution)
    - [Lợi ích so với tải thủ công](#lợi-ích-so-với-tải-thủ-công)
    - [Các bước tích hợp (tóm tắt)](#các-bước-tích-hợp-tóm-tắt)

---

## 1. Build APK trên máy tính cá nhân

### Yêu cầu

| Công cụ | Phiên bản tối thiểu |
|---------|---------------------|
| Android Studio | Ladybug (2024.2) trở lên |
| JDK | 21 (đi kèm Android Studio) |
| Git | Bất kỳ |

### Các bước thực hiện

**Bước 1 — Clone hoặc cập nhật source code**

```bash
git clone https://github.com/<tên-tổ-chức>/emotion-friend.git
cd emotion-friend
```

Nếu đã clone từ trước, cập nhật lên code mới nhất:

```bash
git pull origin master
```

**Bước 2 — Vào thư mục Android**

```bash
cd android-app
```

**Bước 3 — Cấp quyền thực thi cho Gradle Wrapper (macOS / Linux)**

```bash
chmod +x gradlew
```

> Windows không cần bước này.

**Bước 4 — Build APK Debug**

```bash
# macOS / Linux
./gradlew assembleDebug

# Windows (PowerShell)
.\gradlew.bat assembleDebug
```

Quá trình build lần đầu có thể mất 5–10 phút vì Gradle cần tải thư viện. Các lần sau sẽ nhanh hơn nhờ cache.

Khi thấy dòng:

```
BUILD SUCCESSFUL in Xs
```

là build thành công.

---

## 2. Tìm file APK sau khi build

Sau khi build xong, file APK nằm tại:

```
android-app/app/build/outputs/apk/debug/app-debug.apk
```

**Mở bằng File Explorer (Windows):**

```
android-app\app\build\outputs\apk\debug\app-debug.apk
```

**Mở bằng Finder (macOS):**

Nhấn `Cmd + Shift + G` trong Finder, dán đường dẫn trên.

> **Lưu ý:** File `app-debug.apk` được ký bằng debug keystore — chỉ dùng để thử nghiệm, không phát hành lên Google Play.

---

## 3. GitHub Actions tự động build và lưu APK

Mỗi khi bạn push code lên nhánh `main` hoặc `develop`, GitHub Actions sẽ tự động:

1. Khởi động máy ảo Ubuntu trên server GitHub.
2. Cài JDK 17 (Temurin).
3. Chạy kiểm tra code tĩnh (`detekt`).
4. Chạy unit test (`testDebugUnitTest`).
5. Build APK debug (`assembleDebug`).
6. **Lưu APK dưới dạng artifact** trong 14 ngày.

### Cách tải APK từ GitHub Actions

1. Vào trang repository trên GitHub.
2. Click tab **Actions** (thanh menu trên cùng).
3. Chọn workflow run mới nhất có tên **Android CI**.
4. Kéo xuống phần **Artifacts**.
5. Click vào **emotion-friend-debug** để tải file `.zip`.
6. Giải nén file `.zip` → bên trong là `app-debug.apk`.

> **Tip:** Mỗi lần push sẽ tạo một artifact mới. Artifact cũ sẽ tự xoá sau 14 ngày.

---

## 4. Cài APK lên thiết bị Android

### Cách 1 — Cài qua cáp USB (khuyến nghị)

**Bước 1 — Bật chế độ Developer trên điện thoại**

1. Vào **Cài đặt** → **Giới thiệu về điện thoại**.
2. Nhấn **Số phiên bản** (Build number) **7 lần liên tiếp**.
3. Nhập mã PIN nếu được yêu cầu.
4. Quay lại **Cài đặt** → xuất hiện mục **Tùy chọn nhà phát triển**.

**Bước 2 — Bật USB Debugging**

1. Vào **Tùy chọn nhà phát triển**.
2. Bật **Gỡ lỗi qua USB** (USB Debugging).

**Bước 3 — Kết nối và cài APK**

```bash
# Kiểm tra thiết bị đã nhận diện chưa
adb devices

# Cài APK
adb install android-app/app/build/outputs/apk/debug/app-debug.apk
```

Kết quả thành công:

```
Performing Streamed Install
Success
```

### Cách 2 — Cài qua file (không cần máy tính)

1. Chuyển file `app-debug.apk` vào điện thoại (qua Zalo, Google Drive, USB, v.v.).
2. Mở ứng dụng **Quản lý file** trên điện thoại.
3. Tìm file `app-debug.apk` và nhấn vào.
4. Nếu được hỏi "Cho phép cài từ nguồn này?", chọn **Cho phép**.
5. Nhấn **Cài đặt** và chờ hoàn tất.

> **Lưu ý bảo mật:** Chỉ cài APK từ nguồn bạn tin tưởng. APK debug của project này chỉ dùng nội bộ.

---

## 5. Danh sách kiểm tra trước buổi demo

Thực hiện các bước sau trước khi demo cho giáo viên hoặc hội đồng:

### Kiểm tra code

- [ ] `git status` — không có file thay đổi chưa commit.
- [ ] `git log --oneline -5` — 5 commit gần nhất đúng như kế hoạch.
- [ ] GitHub Actions workflow run cuối cùng hiện màu **xanh lá** (✅).

### Kiểm tra APK

- [ ] Build lại APK từ đầu: `.\gradlew.bat clean assembleDebug`.
- [ ] APK cài được trên thiết bị Android thực (không chỉ trên emulator).
- [ ] Mở app — màn hình Splash / Home hiển thị đúng, không crash.

### Kiểm tra tính năng chính

- [ ] Chọn cảm xúc hoạt động (tap vào card cảm xúc).
- [ ] Tình huống học hiển thị đúng câu hỏi và đáp án.
- [ ] Ghi nhật ký cảm xúc lưu được vào Room (kiểm tra offline).
- [ ] Màn hình Thư giãn (RelaxScreen) phát âm thanh hoặc hiện animation.
- [ ] Báo cáo tiến độ hiển thị đúng số liệu.

### Kiểm tra thiết bị demo

- [ ] Điện thoại sạc đủ pin (>80%).
- [ ] Chế độ Máy bay TẮT (nếu app cần kết nối backend).
- [ ] Độ sáng màn hình đủ thấy khi demo.
- [ ] Xoá thông báo rác trên thanh trạng thái.

---

## 6. Kế hoạch nâng cấp: Firebase App Distribution

> Đây là kế hoạch tương lai, chưa triển khai trong phiên bản hiện tại.

**Firebase App Distribution** là dịch vụ miễn phí của Google giúp gửi APK đến người kiểm thử mà không cần lên Google Play.

### Lợi ích so với tải thủ công

| Tính năng | Thủ công (hiện tại) | Firebase App Distribution |
|-----------|---------------------|--------------------------|
| Gửi APK cho nhiều người | Copy file / share link | Mời qua email |
| Thông báo tự động | Không | Có (push notification) |
| Quản lý phiên bản | Thủ công | Tự động theo build |
| Tích hợp CI/CD | Partial | Native |
| Chi phí | Miễn phí | Miễn phí (Spark plan) |

### Các bước tích hợp (tóm tắt)

1. Tạo project trên [Firebase Console](https://console.firebase.google.com/).
2. Thêm app Android với package name `com.emotionfriend`.
3. Tải file `google-services.json` về thư mục `android-app/app/`.
4. Thêm plugin Firebase vào `build.gradle.kts`.
5. Cập nhật GitHub Actions — thêm bước upload lên Firebase sau khi `assembleDebug` thành công:

```yaml
- name: Upload to Firebase App Distribution
  uses: wzieba/Firebase-Distribution-Github-Action@v1
  with:
    appId: ${{ secrets.FIREBASE_APP_ID }}
    token: ${{ secrets.FIREBASE_TOKEN }}
    groups: testers
    file: android-app/app/build/outputs/apk/debug/app-debug.apk
```

6. Thêm `FIREBASE_APP_ID` và `FIREBASE_TOKEN` vào **GitHub Secrets**.

> Xem tài liệu chính thức: [Firebase App Distribution](https://firebase.google.com/docs/app-distribution)

---

*Cập nhật lần cuối: tháng 5, 2026*
