# PROJECT SCOPE — Emotion Friend

> **Phiên bản:** 1.0  
> **Cập nhật lần cuối:** 03/05/2026  
> **Giai đoạn áp dụng:** P6 — Nguyên mẫu phần mềm có thể chạy được  
> **Trạng thái tài liệu:** Đã khoá phạm vi (Scope Locked)

---

## Mục lục

1. [Tổng quan dự án](#1-tổng-quan-dự-án)
2. [Đối tượng người dùng](#2-đối-tượng-người-dùng)
3. [Mục tiêu sản phẩm](#3-mục-tiêu-sản-phẩm)
4. [Phạm vi MVP — P6](#4-phạm-vi-mvp--p6)
   - 4.1 [Màn hình bắt buộc](#41-màn-hình-bắt-buộc)
   - 4.2 [Luồng chức năng bắt buộc](#42-luồng-chức-năng-bắt-buộc)
5. [Camera Practice — Phương án triển khai](#5-camera-practice--phương-án-triển-khai)
6. [Kiến trúc Backend](#6-kiến-trúc-backend)
7. [Ngoài phạm vi P6](#7-ngoài-phạm-vi-p6)
8. [Tiêu chí chấp nhận (Acceptance Criteria)](#8-tiêu-chí-chấp-nhận-acceptance-criteria)
9. [Rủi ro và biện pháp giảm thiểu](#9-rủi-ro-và-biện-pháp-giảm-thiểu)
10. [Danh sách kiểm tra demo (Demo Checklist)](#10-danh-sách-kiểm-tra-demo-demo-checklist)

---

## 1. Tổng quan dự án

**Emotion Friend** là ứng dụng di động giáo dục được thiết kế chuyên biệt cho trẻ em mắc rối loạn phổ tự kỷ (ASD) trong độ tuổi 4–10. Ứng dụng hỗ trợ trẻ nhận diện, hiểu, biểu đạt và điều tiết cảm xúc thông qua phương pháp học dựa trên trò chơi (Game-based Learning) với giao diện thị giác trực quan, nhẹ nhàng và dễ tiếp cận.

| Thuộc tính | Chi tiết |
|---|---|
| **Nền tảng** | Android (Native — Kotlin / Jetpack Compose) |
| **Định hướng UI/UX** | Calm Game-based Learning — màu sắc dịu nhẹ, biểu tượng rõ ràng, tương tác đơn giản |
| **Ngôn ngữ giao diện** | Tiếng Việt (phiên bản MVP) |
| **Mốc hiện tại** | P6 — Nộp nguyên mẫu phần mềm có thể chạy được |

---

## 2. Đối tượng người dùng

### 2.1 Người dùng chính
- **Trẻ em tự kỷ, 4–10 tuổi**
- Khả năng đọc hạn chế; cần hướng dẫn bằng hình ảnh và âm thanh
- Dễ bị kích thích bởi màu sắc chói, âm thanh lớn hoặc giao diện phức tạp
- Cần phản hồi tức thì, rõ ràng và khích lệ

### 2.2 Người dùng phụ
- **Phụ huynh**: theo dõi tiến trình cảm xúc của con, xem nhật ký cảm xúc
- **Giáo viên / chuyên viên trị liệu**: sử dụng ứng dụng như công cụ hỗ trợ trong lớp hoặc buổi trị liệu

---

## 3. Mục tiêu sản phẩm

| Mã | Mục tiêu |
|---|---|
| **G-01** | Giúp trẻ **nhận diện** các cảm xúc cơ bản qua hình ảnh và biểu cảm khuôn mặt |
| **G-02** | Giúp trẻ **hiểu** ngữ cảnh xã hội gắn với từng cảm xúc |
| **G-03** | Tạo môi trường an toàn để trẻ **biểu đạt** cảm xúc của bản thân |
| **G-04** | Cung cấp kỹ thuật đơn giản giúp trẻ **điều tiết** cảm xúc tiêu cực |
| **G-05** | Cho phép phụ huynh và giáo viên **theo dõi tiến trình** cảm xúc của trẻ theo thời gian |

---

## 4. Phạm vi MVP — P6

> Tất cả các hạng mục trong mục này là **bắt buộc** đối với bản demo P6.  
> Mọi thay đổi phạm vi phải được team lead phê duyệt bằng văn bản.

### 4.1 Màn hình bắt buộc

| STT | Màn hình | Mã màn hình | Mô tả ngắn |
|---|---|---|---|
| 1 | **Home** | `SCR-HOME` | Màn hình chào, điều hướng trung tâm tới các module |
| 2 | **Learn Emotion** | `SCR-LEARN` | Học nhận biết cảm xúc qua flashcard và quiz |
| 3 | **Situation** | `SCR-SITU` | Xem tình huống xã hội, chọn cảm xúc phù hợp |
| 4 | **Camera Practice** | `SCR-CAM` | Luyện biểu đạt cảm xúc qua camera (mock hoặc CameraX preview) |
| 5 | **Relax** | `SCR-RELAX` | Kỹ thuật thở / giảm căng thẳng có hướng dẫn hoạt hình |
| 6 | **Personal Emotion / Journal** | `SCR-JOUR` | Trẻ ghi nhận cảm xúc hiện tại; lưu nhật ký cảm xúc |
| 7 | **Progress** | `SCR-PROG` | Biểu đồ / tóm tắt lịch sử cảm xúc đã ghi nhận |

> Tất cả 7 màn hình phải **điều hướng được** và **hiển thị đúng layout** trong bản demo P6, kể cả màn hình chưa có đủ dữ liệu thật.

---

### 4.2 Luồng chức năng bắt buộc

Phải có tối thiểu **3 luồng hoàn chỉnh** (end-to-end runnable) thoả mãn yêu cầu P6.

---

#### Luồng 1 — Learn Emotion (`FLOW-01`)

**Mục tiêu:** Trẻ học nhận biết một cảm xúc qua flashcard tương tác.

```
[SCR-HOME] ──► [SCR-LEARN: Chọn cảm xúc / cấp độ]
                    │
                    ▼
             [Hiển thị Flashcard]
             (hình ảnh + tên cảm xúc + âm thanh)
                    │
                    ▼
             [Câu hỏi Quiz: Chọn đáp án đúng]
             (hiển thị 3–4 lựa chọn hình ảnh)
                    │
             ┌──────┴──────┐
           Đúng           Sai
             │               │
             ▼               ▼
    [Phản hồi Đúng]   [Phản hồi Sai]
    (hoạt ảnh khen,   (gợi ý nhẹ nhàng,
     âm thanh vui)    cho thử lại)
                    │
                    ▼
          [Tiếp tục / Kết thúc vòng]
```

**Tiêu chí hoàn thành luồng:**
- [ ] Flashcard hiển thị đúng hình ảnh và tên cảm xúc
- [ ] Có ít nhất 3 đáp án lựa chọn
- [ ] Phản hồi đúng/sai hiển thị rõ ràng
- [ ] Có thể hoàn thành ít nhất 3 câu hỏi liên tiếp không bị crash

---

#### Luồng 2 — Situation (`FLOW-02`)

**Mục tiêu:** Trẻ hiểu cảm xúc trong bối cảnh tình huống xã hội.

```
[SCR-HOME] ──► [SCR-SITU: Danh sách tình huống]
                    │
                    ▼
             [Hiển thị tình huống]
             (hình minh hoạ + mô tả ngắn bằng
              text và audio tuỳ chọn)
                    │
                    ▼
             [Câu hỏi: "Bạn nhỏ đang cảm thấy gì?"]
             (chọn 1 trong 3–4 cảm xúc)
                    │
             ┌──────┴──────┐
           Đúng           Sai
             │               │
             ▼               ▼
    [Giải thích đúng]  [Giải thích tại sao]
    (ngắn gọn, dễ hiểu, có hình minh hoạ)
                    │
                    ▼
          [Tiếp tục tình huống tiếp theo]
```

**Tiêu chí hoàn thành luồng:**
- [ ] Có ít nhất 3 tình huống xã hội khác nhau
- [ ] Mỗi tình huống có hình ảnh minh hoạ rõ ràng
- [ ] Phần giải thích hiển thị sau khi chọn đáp án (đúng hoặc sai)
- [ ] Điều hướng sang tình huống kế tiếp hoạt động bình thường

---

#### Luồng 3 — Personal Emotion + Progress (`FLOW-03`)

**Mục tiêu:** Trẻ ghi nhận cảm xúc hiện tại; dữ liệu được lưu cục bộ và hiển thị trên màn hình Progress.

```
[SCR-HOME] ──► [SCR-JOUR: Hôm nay bạn cảm thấy thế nào?]
                    │
                    ▼
             [Chọn cảm xúc hiện tại]
             (grid emoji/hình ảnh cảm xúc)
                    │
                    ▼
             [Xác nhận & Lưu]
             (lưu entry: {timestamp, emotionId}
              vào Room / SharedPreferences local)
                    │
                    ▼
             [Thông điệp khích lệ ngắn]
                    │
                    ▼
[SCR-HOME] ──► [SCR-PROG: Xem Progress]
             (biểu đồ / danh sách entries đã lưu
              phản ánh bản ghi vừa tạo)
```

**Tiêu chí hoàn thành luồng:**
- [ ] Giao diện chọn cảm xúc hiển thị ít nhất 6 cảm xúc
- [ ] Entry được lưu vào bộ nhớ cục bộ (Room DB hoặc tương đương)
- [ ] Màn hình Progress hiển thị lại đúng entry vừa lưu ngay sau khi quay lại
- [ ] Dữ liệu không mất sau khi tắt và mở lại ứng dụng

---

## 5. Camera Practice — Phương án triển khai

> Do ràng buộc thời gian P6, tính năng nhận diện cảm xúc bằng AI **không thuộc phạm vi MVP**.  
> Camera Practice được triển khai theo một trong hai phương án sau:

### Phương án A — CameraX Preview (Ưu tiên)

| Bước | Mô tả kỹ thuật |
|---|---|
| Tích hợp | Sử dụng `androidx.camera:camera-camera2` và `camera-lifecycle` |
| Hiển thị | `PreviewView` hiển thị luồng camera trực tiếp |
| Prompt | Hiển thị overlay: *"Hãy thử biểu lộ cảm xúc [VUI / BUỒN / ...]"* |
| Feedback | Mock feedback tĩnh sau 3–5 giây: *"Tuyệt vời! Bạn đã thể hiện rất tốt!"* |
| Quyền | Yêu cầu `CAMERA` permission; xử lý trường hợp bị từ chối gracefully |

### Phương án B — Mock Camera UI (Fallback)

Sử dụng khi thiết bị demo không hỗ trợ hoặc permission bị từ chối:

- Hiển thị placeholder hình ảnh khuôn mặt hoạt hình
- Nút "Thử lại" → hiển thị animation và mock feedback
- Giao diện đầy đủ, trải nghiệm người dùng liền mạch

> **Lưu ý bàn giao:** Tài liệu phải ghi rõ phương án nào được triển khai trong bản demo và lý do.

---

## 6. Kiến trúc Backend

### 6.1 Nguyên tắc kiến trúc

| Nguyên tắc | Mô tả |
|---|---|
| **Tách biệt dữ liệu** | MySQL **chỉ** được truy cập bởi backend; ứng dụng Android **không bao giờ** kết nối trực tiếp tới database |
| **Backend tuỳ chọn** | Với bản demo P6, backend chỉ cần ở mức skeleton — không bắt buộc phải deploy production |
| **Offline-first** | Dữ liệu MVP lưu cục bộ (Room DB); backend sync là tính năng giai đoạn sau |

### 6.2 Sơ đồ kiến trúc tổng quan

```
┌─────────────────────────┐
│   Android App (Kotlin)  │
│  ┌─────────────────┐    │
│  │   Room Database │    │  ← Lưu trữ cục bộ (MVP)
│  │  (local SQLite) │    │
│  └─────────────────┘    │
│  ┌─────────────────┐    │
│  │   API Client    │    │  ← Retrofit / OkHttp (skeleton)
│  │  (Retrofit)     │    │
│  └────────┬────────┘    │
└───────────┼─────────────┘
            │ HTTPS / REST
            ▼
┌─────────────────────────┐
│   Backend (Skeleton)    │
│   Spring Boot / Node    │
│  ┌─────────────────┐    │
│  │   REST API      │    │
│  │   Controllers   │    │
│  └────────┬────────┘    │
└───────────┼─────────────┘
            │ JDBC / ORM
            ▼
┌─────────────────────────┐
│      MySQL Database     │
│   (chỉ backend truy cập)│
└─────────────────────────┘
```

### 6.3 Yêu cầu backend tối thiểu cho P6

Backend skeleton phải có ít nhất:
- Cấu trúc project khởi tạo được (Maven/Gradle build thành công)
- Định nghĩa ít nhất 1 endpoint REST (ví dụ: `GET /api/health`)
- Schema database cơ bản (script SQL tạo bảng)
- README hướng dẫn cách chạy

---

## 7. Ngoài phạm vi P6

Các hạng mục sau **không được triển khai** trong P6 và sẽ xem xét ở giai đoạn sau:

| Hạng mục | Lý do hoãn |
|---|---|
| **Hệ thống đăng nhập đầy đủ** | Không cần thiết cho demo; thêm độ phức tạp không cần thiết |
| **Nhận diện cảm xúc AI thật** | Yêu cầu model ML, dataset huấn luyện, và thời gian tích hợp vượt quá P6 |
| **Push Notification** | Phụ thuộc backend hoàn chỉnh và FCM setup |
| **CMS / Admin Panel** | Dành cho giai đoạn vận hành thực tế |
| **Đồng bộ đa thiết bị** | Phụ thuộc authentication và backend đầy đủ |
| **Phát hành lên Production (Play Store)** | Ngoài phạm vi học thuật |
| **Đa ngôn ngữ (i18n)** | Tiếng Anh và các ngôn ngữ khác — giai đoạn sau |
| **Accessibility nâng cao** | TalkBack, switch access — giai đoạn sau |

> Mọi đề xuất bổ sung tính năng từ thành viên phải được đánh giá và phê duyệt trước khi đưa vào sprint, tránh scope creep.

---

## 8. Tiêu chí chấp nhận (Acceptance Criteria)

### 8.1 Tiêu chí toàn ứng dụng

| Mã | Tiêu chí | Mức độ |
|---|---|---|
| `AC-APP-01` | Ứng dụng cài đặt và khởi động thành công trên Android 8.0+ (API 26+) | **Bắt buộc** |
| `AC-APP-02` | Không có crash nghiêm trọng (Force Close) trong suốt luồng demo | **Bắt buộc** |
| `AC-APP-03` | Tất cả 7 màn hình điều hướng được từ Home | **Bắt buộc** |
| `AC-APP-04` | Giao diện hiển thị đúng trên màn hình 5.0" – 6.7" | **Bắt buộc** |
| `AC-APP-05` | Toàn bộ text đọc được, không bị cắt xén | **Bắt buộc** |
| `AC-APP-06` | Màu sắc và hình ảnh phù hợp hướng dẫn Calm Game-based Learning | **Bắt buộc** |

### 8.2 Tiêu chí theo luồng

| Mã | Luồng | Tiêu chí | Mức độ |
|---|---|---|---|
| `AC-F01-01` | FLOW-01 | Flashcard hiển thị hình ảnh + tên cảm xúc | **Bắt buộc** |
| `AC-F01-02` | FLOW-01 | Phản hồi đúng/sai hiển thị rõ ràng, khác biệt | **Bắt buộc** |
| `AC-F01-03` | FLOW-01 | Hoàn thành 3+ câu hỏi liên tiếp không lỗi | **Bắt buộc** |
| `AC-F02-01` | FLOW-02 | Có ít nhất 3 tình huống khác nhau | **Bắt buộc** |
| `AC-F02-02` | FLOW-02 | Giải thích hiển thị sau mỗi lần chọn đáp án | **Bắt buộc** |
| `AC-F03-01` | FLOW-03 | Entry cảm xúc được lưu sau khi xác nhận | **Bắt buộc** |
| `AC-F03-02` | FLOW-03 | Progress phản ánh đúng dữ liệu đã lưu | **Bắt buộc** |
| `AC-F03-03` | FLOW-03 | Dữ liệu không mất sau khi khởi động lại app | **Bắt buộc** |
| `AC-CAM-01` | Camera | Camera preview hoặc mock UI hiển thị đúng | **Bắt buộc** |
| `AC-CAM-02` | Camera | Prompt cảm xúc hiển thị overlay | **Bắt buộc** |
| `AC-CAM-03` | Camera | Mock feedback xuất hiện sau tương tác | **Bắt buộc** |
| `AC-CAM-04` | Camera | Ứng dụng không crash khi từ chối camera permission | **Bắt buộc** |
| `AC-BK-01` | Backend | Backend skeleton build thành công | **Khuyến nghị** |
| `AC-BK-02` | Backend | Endpoint `/api/health` trả về `200 OK` | **Khuyến nghị** |

### 8.3 Tiêu chí UX

| Mã | Tiêu chí | Mức độ |
|---|---|---|
| `AC-UX-01` | Không có màn hình trống (blank screen) khi chờ tải dữ liệu | **Bắt buộc** |
| `AC-UX-02` | Nút Back / điều hướng hoạt động nhất quán | **Bắt buộc** |
| `AC-UX-03` | Thông điệp phản hồi (đúng/sai/khích lệ) có hình ảnh đi kèm | **Bắt buộc** |
| `AC-UX-04` | Không có lỗi layout trên cả chế độ portrait và landscape | **Khuyến nghị** |

---

## 9. Rủi ro và biện pháp giảm thiểu

| Mã | Rủi ro | Xác suất | Mức độ ảnh hưởng | Biện pháp giảm thiểu |
|---|---|---|---|---|
| `RISK-01` | Tích hợp CameraX gặp lỗi tương thích thiết bị | Trung bình | Cao | Chuẩn bị sẵn Phương án B (Mock Camera UI); test sớm trên thiết bị demo |
| `RISK-02` | Scope creep — thêm tính năng ngoài MVP | Cao | Trung bình | Khoá phạm vi tài liệu này; mọi thay đổi phải qua review team lead |
| `RISK-03` | Thiếu tài nguyên thiết kế (hình ảnh cảm xúc, icon) | Trung bình | Trung bình | Sử dụng asset placeholder sớm; phân công designer song song với dev |
| `RISK-04` | Dữ liệu Room DB bị mất khi update app | Thấp | Cao | Viết migration script từ schema v1; test kỹ trước demo |
| `RISK-05` | Backend skeleton không sẵn sàng cho demo | Trung bình | Thấp | Backend là tuỳ chọn cho P6; app Android hoạt động offline-first |
| `RISK-06` | Giao diện không phù hợp với trẻ tự kỷ (quá phức tạp / gây kích động) | Thấp | Cao | Kiểm tra thiết kế theo hướng dẫn AAC/PCS; lấy feedback từ giáo viên chuyên biệt nếu có |
| `RISK-07` | Thiếu thời gian test trên thiết bị thật | Trung bình | Cao | Dành ít nhất 3 ngày trước ngày nộp để test trên thiết bị thật |

---

## 10. Danh sách kiểm tra demo (Demo Checklist)

> Phải hoàn thành toàn bộ checklist trước khi nộp P6.  
> Ký tên xác nhận bên cạnh mỗi mục sau khi kiểm tra.

### 10.1 Kiểm tra kỹ thuật

- [ ] APK build thành công ở chế độ release (hoặc debug cho P6)
- [ ] Ứng dụng cài đặt được trên thiết bị demo (không dùng emulator cho buổi thuyết trình)
- [ ] Không có lỗi crash trong tất cả 3 luồng bắt buộc
- [ ] Room DB lưu và đọc dữ liệu đúng
- [ ] Camera permission flow hoạt động (cả khi chấp nhận lẫn từ chối)
- [ ] Toàn bộ hình ảnh và icon hiển thị đúng (không có ảnh vỡ / placeholder còn sót)
- [ ] Không có hardcoded credential hoặc API key trong source code
- [ ] ProGuard / R8 không làm vỡ app (nếu enable)

### 10.2 Kiểm tra giao diện

- [ ] Tất cả 7 màn hình điều hướng được
- [ ] Không có text bị cắt xén hoặc overflow
- [ ] Màu sắc nhất quán theo design system
- [ ] Nút và vùng chạm có kích thước tối thiểu 48×48 dp (theo Material Design)
- [ ] Trạng thái loading không để màn hình trắng

### 10.3 Kiểm tra luồng demo

- [ ] **FLOW-01**: Hoàn thành toàn bộ 1 vòng Learn Emotion (flashcard → quiz → feedback)
- [ ] **FLOW-02**: Hoàn thành ít nhất 2 tình huống Situation liên tiếp
- [ ] **FLOW-03**: Ghi nhận cảm xúc → thoát app → mở lại → Progress hiển thị đúng
- [ ] Camera Practice hiển thị prompt và mock feedback

### 10.4 Kiểm tra nội dung

- [ ] Tất cả văn bản tiếng Việt không có lỗi chính tả
- [ ] Nội dung hướng dẫn phù hợp với nhóm tuổi 4–10
- [ ] Không có placeholder text (Lorem Ipsum, "TODO", "Test") còn sót trong bản demo

### 10.5 Kiểm tra backend (nếu có)

- [ ] Backend build và start thành công
- [ ] `GET /api/health` trả về `200 OK`
- [ ] Schema SQL đã được review và không có lỗi syntax

### 10.6 Chuẩn bị buổi thuyết trình

- [ ] Thiết bị demo đã sạc đầy (>80%)
- [ ] Chế độ Không làm phiền (Do Not Disturb) đã bật
- [ ] Đã xoá thông báo không liên quan trên thiết bị
- [ ] Backup APK trên USB/cloud phòng trường hợp sự cố
- [ ] Thành viên phụ trách demo đã luyện tập ít nhất 1 lần trọn vẹn

---

## Lịch sử thay đổi tài liệu

| Phiên bản | Ngày | Người thực hiện | Nội dung thay đổi |
|---|---|---|---|
| 1.0 | 03/05/2026 | Tech Lead | Tạo tài liệu, khoá phạm vi MVP P6 |

---

*Tài liệu này là tài liệu kiểm soát phạm vi chính thức cho giai đoạn P6. Mọi thay đổi phải được ghi nhận vào Lịch sử thay đổi và được team lead phê duyệt.*
