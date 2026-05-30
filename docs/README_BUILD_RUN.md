# Hướng dẫn Build & Chạy Ứng dụng Android (Emotion Friend)

## 1. Yêu cầu hệ thống
- Máy tính cài đặt **Java JDK 17** hoặc mới hơn
- **Android Studio** (khuyến nghị) hoặc chỉ cần Android SDK + Platform Tools
- Thiết bị Android (điện thoại) đã bật chế độ **Developer Options** và **USB Debugging**
- Cáp USB kết nối máy tính với điện thoại

## 2. Cài đặt Android SDK & Platform Tools
- Nếu đã cài Android Studio, SDK và Platform Tools đã có sẵn.
- Nếu chưa, tải Platform Tools tại: https://developer.android.com/studio/releases/platform-tools

## 3. Bật Developer Options & USB Debugging trên điện thoại
1. Vào **Cài đặt** > **Giới thiệu về điện thoại**
2. Nhấn nhiều lần vào **Số bản dựng** (Build number) để bật Developer Options
3. Vào **Cài đặt cho nhà phát triển** (Developer options)
4. Bật **Gỡ lỗi USB** (USB debugging)

## 4. Kết nối thiết bị với máy tính
- Cắm cáp USB, chọn chế độ **Truyền file (File Transfer)** nếu được hỏi.
- Kiểm tra thiết bị đã nhận bằng lệnh:

```powershell
adb devices
```
- Nếu thấy thiết bị hiện trạng thái `device` là OK.

## 5. Build & Cài đặt APK lên điện thoại
### a. Mở Terminal/PowerShell tại thư mục `android-app`

```powershell
cd "D:\_CODE_BANK\Project_\_Best Project_\emotion-friend\android-app"
```

### b. Build APK debug

```powershell
./gradlew.bat :app:assembleDebug
```

### c. Cài đặt APK lên điện thoại

```powershell
./gradlew.bat :app:installDebug
```

Hoặc dùng adb trực tiếp (nếu cần):

```powershell
adb install -r .\app\build\outputs\apk\debug\app-debug.apk
```

## 6. Khắc phục lỗi thường gặp
- Nếu báo **No connected devices!**: Kiểm tra lại cáp, bật USB Debugging, xác nhận trên điện thoại nếu có popup.
- Nếu **adb** không nhận, cần thêm Platform Tools vào PATH hoặc dùng đường dẫn đầy đủ tới adb.exe.
- Nếu build lỗi do thiếu SDK, cài đặt đủ Android SDK qua Android Studio hoặc SDK Manager.

## 7. Chạy app trên điện thoại
- Sau khi cài đặt thành công, tìm app "Emotion Friend" trên điện thoại và mở để sử dụng.

---
**Mọi thắc mắc, xem thêm tài liệu trong thư mục `docs/` hoặc liên hệ nhóm phát triển.**

