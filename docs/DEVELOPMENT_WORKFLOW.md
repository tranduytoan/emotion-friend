# DEVELOPMENT WORKFLOW — Emotion Friend

> **Phiên bản:** 1.0  
> **Cập nhật lần cuối:** 03/05/2026  
> **Áp dụng cho:** Toàn bộ thành viên nhóm phát triển Emotion Friend

---

## Mục lục

1. [Phân công kỹ thuật](#1-phân-công-kỹ-thuật)
2. [Mô hình nhánh (Branch Strategy)](#2-mô-hình-nhánh-branch-strategy)
3. [Quy ước commit](#3-quy-ước-commit)
4. [Quy trình Pull Request](#4-quy-trình-pull-request)
5. [Template Pull Request](#5-template-pull-request)
6. [Checklist code review](#6-checklist-code-review)
7. [Checklist merge](#7-checklist-merge)
8. [Giải quyết xung đột (Conflict Resolution)](#8-giải-quyết-xung-đột-conflict-resolution)
9. [Quy tắc bổ sung](#9-quy-tắc-bổ-sung)

---

## 1. Phân công kỹ thuật

Mỗi thành viên **sở hữu** (owns) một hoặc nhiều mảng kỹ thuật. "Sở hữu" có nghĩa là người đó chịu trách nhiệm chính: thiết kế, triển khai, review PR liên quan và đảm bảo chất lượng của mảng đó.

| Thành viên | Mảng kỹ thuật phụ trách | Nhánh feature tiêu biểu |
|---|---|---|
| **Nghĩa** | Kiến trúc tổng thể, tích hợp module, DevOps, backend skeleton, merge cuối | `feature/project-setup`, `feature/backend-skeleton`, `feature/di-hilt-setup` |
| **Duy** | Design system, shared UI components, màn hình Home, màn hình Learn Emotion | `feature/design-system`, `feature/scr-home`, `feature/flow-learn-emotion` |
| **Dung** | Luồng Situation, logic phản hồi đúng/sai, màn hình Situation | `feature/flow-situation`, `feature/situation-feedback` |
| **Hiệp** | Camera Practice (CameraX / mock UI), màn hình Relax | `feature/scr-camera-practice`, `feature/scr-relax` |
| **Toàn** | Room Database, DataStore, màn hình Progress, QA checklist demo | `feature/room-setup`, `feature/flow-journal-progress`, `feature/scr-progress` |

> **Nguyên tắc:** Không ai tự ý sửa code trong mảng của thành viên khác mà không thông báo và được đồng ý trước. Nếu cần thay đổi cross-team, tạo issue thảo luận trước.

---

## 2. Mô hình nhánh (Branch Strategy)

### 2.1 Sơ đồ tổng quan

```
main
 └── develop
      ├── feature/project-setup          (Nghĩa)
      ├── feature/design-system          (Duy)
      ├── feature/scr-home               (Duy)
      ├── feature/flow-learn-emotion     (Duy)
      ├── feature/flow-situation         (Dung)
      ├── feature/situation-feedback     (Dung)
      ├── feature/scr-camera-practice    (Hiệp)
      ├── feature/scr-relax              (Hiệp)
      ├── feature/room-setup             (Toàn)
      ├── feature/flow-journal-progress  (Toàn)
      ├── feature/scr-progress           (Toàn)
      └── fix/<tên-lỗi>                  (ai phụ trách mảng đó)
```

### 2.2 Định nghĩa từng loại nhánh

| Nhánh | Mục đích | Ai quản lý | Merge vào |
|---|---|---|---|
| `main` | Code ổn định — đã demo / nộp | Nghĩa | — |
| `develop` | Nhánh tích hợp — base cho mọi feature | Nghĩa | `main` (qua PR, chỉ khi chuẩn bị release / nộp) |
| `feature/<tên>` | Phát triển một tính năng / màn hình | Thành viên phụ trách | `develop` (qua PR) |
| `fix/<tên>` | Sửa lỗi phát hiện trong `develop` | Thành viên phụ trách | `develop` (qua PR) |

### 2.3 Quy tắc bắt buộc

- ❌ **Không được push thẳng lên `main`** — kể cả team lead.
- ❌ **Không được push thẳng lên `develop`** — mọi thay đổi phải qua PR.
- ✅ Chỉ `main` và `develop` tồn tại lâu dài. Nhánh `feature/*` và `fix/*` xoá sau khi merge.
- ✅ Luôn tạo nhánh từ `develop` (không phải từ `main`):

```bash
git checkout develop
git pull origin develop
git checkout -b feature/<tên-tính-năng>
```

### 2.4 Đặt tên nhánh

```
feature/<module>-<mô-tả-ngắn>
fix/<module>-<mô-tả-ngắn>
```

Ví dụ hợp lệ:
```
feature/scr-home
feature/flow-learn-emotion
feature/room-emotion-log-table
fix/journal-entry-not-persisted
fix/camera-permission-crash
```

Quy tắc đặt tên:
- Dùng chữ thường, phân cách bằng dấu gạch ngang `-`
- Không dùng dấu cách, ký tự đặc biệt, viết hoa
- Ngắn gọn, đủ mô tả (≤ 50 ký tự)

---

## 3. Quy ước commit

Dự án tuân theo **Conventional Commits** ([conventionalcommits.org](https://www.conventionalcommits.org)).

### 3.1 Cú pháp

```
<type>(<scope>): <mô tả ngắn>

[body — tuỳ chọn: giải thích WHY, không phải WHAT]

[footer — tuỳ chọn: BREAKING CHANGE, Closes #<issue>]
```

### 3.2 Các type hợp lệ

| Type | Ý nghĩa | Ví dụ |
|---|---|---|
| `feat` | Thêm tính năng mới | `feat(learn-emotion): add flashcard screen` |
| `fix` | Sửa lỗi | `fix(room): add migration v1 to v2` |
| `docs` | Thay đổi tài liệu | `docs: update README setup section` |
| `chore` | Cập nhật dependencies, cấu hình build | `chore(deps): upgrade compose bom to 2024.09.00` |
| `refactor` | Tái cấu trúc, không thêm tính năng / sửa lỗi | `refactor(journal): extract EmotionGrid composable` |
| `test` | Thêm hoặc sửa test | `test(room): add unit tests for EmotionLogDao` |
| `ci` | Thay đổi CI/CD | `ci: add GitHub Actions workflow for lint` |
| `style` | Format code, không thay đổi logic | `style: apply ktlint formatting` |

### 3.3 Scope khuyến nghị

| Scope | Module / Mảng tương ứng |
|---|---|
| `home` | Màn hình Home |
| `learn-emotion` | Module Learn Emotion |
| `situation` | Module Situation |
| `camera` | Camera Practice |
| `relax` | Màn hình Relax |
| `journal` | Personal Emotion / Journal |
| `progress` | Màn hình Progress |
| `room` | Room Database |
| `datastore` | DataStore |
| `di` | Hilt / Dependency Injection |
| `nav` | Navigation |
| `design` | Design system, shared UI |
| `backend` | Backend API |
| `infra` | Docker, infra |
| `deps` | Dependencies |

### 3.4 Ví dụ commit thực tế

```bash
feat(situation): add social scenario list screen with image cards
feat(camera): integrate CameraX preview with emotion prompt overlay
fix(room): resolve crash on first-launch database creation
refactor(learn-emotion): move quiz logic to LearnEmotionViewModel
test(room): add DAO tests for emotion log insert and query
docs: define team development workflow
chore(deps): upgrade kotlin to 2.0.0
```

### 3.5 Quy tắc viết commit

- ✅ Dùng tiếng Anh cho commit message (nhất quán với code)
- ✅ Mô tả ngắn dùng **imperative mood**: *"add"*, *"fix"*, *"update"* — không phải *"added"*, *"fixed"*
- ✅ Độ dài mô tả ngắn tối đa **72 ký tự**
- ❌ Không viết hoa chữ đầu mô tả ngắn
- ❌ Không kết thúc mô tả ngắn bằng dấu chấm

---

## 4. Quy trình Pull Request

### 4.1 Luồng chuẩn

```
1. Tạo nhánh từ develop
        │
        ▼
2. Phát triển & commit theo quy ước
        │
        ▼
3. Cập nhật nhánh từ develop trước khi tạo PR
   (git rebase develop hoặc git merge develop)
        │
        ▼
4. Push nhánh lên remote
   git push origin feature/<tên>
        │
        ▼
5. Tạo Pull Request trên GitHub/GitLab
   - Title theo commit convention
   - Điền đầy đủ PR template
   - Gắn label, assign reviewer
        │
        ▼
6. Reviewer kiểm tra (xem Checklist code review)
        │
   ┌────┴────┐
  Pass    Request Changes
   │           │
   ▼           ▼
7. Merge   Tác giả sửa & push thêm commits
   vào develop  (không force push sau khi có review)
        │
        ▼
8. Xoá nhánh feature sau khi merge
```

### 4.2 Phân quyền review

| PR merge vào | Ai review | Ai merge |
|---|---|---|
| `develop` | Ít nhất **1 thành viên khác** (không phải tác giả) | Tác giả sau khi có approval |
| `main` | **Nghĩa** review và merge | Nghĩa |

### 4.3 Thời gian phản hồi

- Reviewer phải phản hồi PR trong vòng **24 giờ** kể từ khi được assign.
- Nếu quá 24 giờ không có phản hồi, tác giả ping trực tiếp trên kênh nhóm.

---

## 5. Template Pull Request

> Khi tạo PR trên GitHub, copy nội dung dưới đây vào phần description.

```markdown
## Mô tả
<!-- Tóm tắt những gì PR này thay đổi và TẠI SAO -->

## Phạm vi thay đổi (Scope)
<!-- Module / màn hình / mảng kỹ thuật bị ảnh hưởng -->
- [ ] Home
- [ ] Learn Emotion
- [ ] Situation
- [ ] Camera Practice
- [ ] Relax
- [ ] Journal / Personal Emotion
- [ ] Progress
- [ ] Room / DataStore
- [ ] Navigation
- [ ] Design System
- [ ] Backend
- [ ] Infra / Config

## Screenshots / Demo
<!-- Chụp màn hình hoặc quay video ngắn cho thay đổi UI.
     Nếu không có thay đổi UI, ghi "N/A". -->

| Trước | Sau |
|---|---|
| (ảnh trước) | (ảnh sau) |

## Ghi chú kiểm thử (Test Notes)
<!-- Mô tả các bước để reviewer tự kiểm tra thay đổi này -->
1. Checkout nhánh `feature/...`
2. Build và chạy app
3. Điều hướng đến màn hình ...
4. Thực hiện ...
5. Kết quả kỳ vọng: ...

**Thiết bị / emulator đã kiểm tra:**
- [ ] Emulator API 26
- [ ] Emulator API 35
- [ ] Thiết bị thật: ___________

## Rủi ro tiềm ẩn
<!-- Có thể ảnh hưởng đến module khác không? Database migration? -->

## Checklist tác giả
- [ ] Code compile và chạy không lỗi
- [ ] Không có hardcoded string, credential, hoặc TODO còn sót
- [ ] Đã rebase / merge `develop` mới nhất vào nhánh này
- [ ] Commit message theo đúng Conventional Commits
- [ ] Đã tự review diff trước khi tạo PR

## Issues liên quan
Closes #<!-- số issue -->
```

---

## 6. Checklist code review

Reviewer sử dụng checklist này khi review PR. Mỗi mục phải được kiểm tra trước khi approve.

### 6.1 Correctness (Đúng đắn)

- [ ] Logic xử lý đúng với yêu cầu mô tả trong PR
- [ ] Không có lỗi crash tiềm ẩn (NullPointerException, IndexOutOfBounds...)
- [ ] Xử lý trường hợp đặc biệt: list rỗng, null, error state
- [ ] Không có vòng lặp vô tận hoặc coroutine bị leak

### 6.2 Architecture (Kiến trúc)

- [ ] Code đặt đúng layer: UI chỉ trong `ui/`, business logic trong `viewmodel/` hoặc `domain/`, data access trong `data/`
- [ ] ViewModel không import Android framework trực tiếp (không dùng `Context` trong VM)
- [ ] Repository là điểm truy cập duy nhất vào Room / DataStore / API
- [ ] Hilt injection dùng đúng scope (`@Singleton`, `@ViewModelScoped`...)

### 6.3 UI / Compose

- [ ] Composable function nhỏ, đơn trách nhiệm
- [ ] Không hardcode màu sắc, kích thước — dùng `MaterialTheme` token
- [ ] Preview annotation có cho composable chính
- [ ] Không có logic phức tạp bên trong composable (move to VM)
- [ ] Vùng chạm tối thiểu 48×48 dp

### 6.4 Database / Persistence

- [ ] Mọi thay đổi schema Room kèm theo migration script
- [ ] Query Room chạy trên `Dispatchers.IO`, không trên main thread
- [ ] Không lưu dữ liệu nhạy cảm dưới dạng plaintext trong SharedPreferences / DataStore

### 6.5 Code Quality

- [ ] Không có dead code, commented-out code, hoặc debug log còn sót
- [ ] Tên biến / hàm / class rõ ràng, theo Kotlin naming convention
- [ ] Không có magic number — dùng hằng số có tên
- [ ] Không có string tiếng Việt hardcode trong code — dùng `strings.xml`

### 6.6 Security

- [ ] Không có API key, password, token trong source code
- [ ] `AndroidManifest.xml` không khai báo permission không cần thiết
- [ ] Input từ người dùng được validate trước khi xử lý

### 6.7 Backward Compatibility

- [ ] Thay đổi không làm vỡ các màn hình / flow đã hoạt động
- [ ] Nếu có thay đổi Room schema, migration đã được viết và test

---

## 7. Checklist merge

Trước khi nhấn **Merge** trên PR, tác giả (sau khi có approval) xác nhận:

### 7.1 Merge `feature/*` → `develop`

- [ ] PR có ít nhất **1 approval** từ thành viên khác
- [ ] Không còn review comment nào ở trạng thái "unresolved"
- [ ] CI/CD (nếu có) pass hoặc không có lỗi mới so với `develop`
- [ ] Đã rebase / merge `develop` mới nhất — **không có conflict**
- [ ] Commit history gọn gàng (squash nếu có quá nhiều WIP commit)
- [ ] PR title đúng format Conventional Commits
- [ ] Issue liên quan đã được đóng bằng `Closes #<number>`

### 7.2 Merge `develop` → `main` (chỉ Nghĩa thực hiện)

- [ ] Tất cả feature của sprint đã merge vào `develop`
- [ ] Toàn bộ 3 luồng runnable đã test thủ công trên `develop`
- [ ] Không có lỗi crash trên thiết bị demo thật
- [ ] Version code / version name trong `build.gradle.kts` đã cập nhật
- [ ] Tag release đã tạo: `git tag -a v<version> -m "Release v<version>"`
- [ ] APK debug đã export và kiểm tra lần cuối

---

## 8. Giải quyết xung đột (Conflict Resolution)

### 8.1 Nguyên tắc

- Người **tạo nhánh feature** chịu trách nhiệm giải quyết conflict khi rebase/merge `develop` vào nhánh của mình — không phải reviewer.
- Không giải quyết conflict bằng cách chấp nhận toàn bộ một phía (`--ours` hoặc `--theirs`) mà không hiểu rõ nội dung.
- Khi không chắc chắn, **hỏi thành viên sở hữu mảng bị conflict** trước khi quyết định.

### 8.2 Quy trình giải quyết

```bash
# 1. Cập nhật develop mới nhất
git checkout develop
git pull origin develop

# 2. Quay lại nhánh feature và rebase
git checkout feature/<tên>
git rebase develop

# 3. Nếu có conflict, mở file bị conflict và giải quyết thủ công
# Tìm các marker: <<<<<<, =======, >>>>>>>
# Giữ lại code đúng, xoá marker

# 4. Sau khi resolve từng file
git add <file-đã-resolve>
git rebase --continue

# 5. Nếu muốn huỷ và quay về trạng thái ban đầu
git rebase --abort

# 6. Push lên remote (cần --force-with-lease vì lịch sử đã rebase)
git push origin feature/<tên> --force-with-lease
```

> ⚠️ **Chỉ dùng `--force-with-lease`**, không dùng `--force`. Lệnh này an toàn hơn vì chỉ force push nếu không có commit mới từ người khác trên nhánh đó.

### 8.3 Conflict thường gặp và cách xử lý

| File conflict | Nguyên nhân phổ biến | Cách xử lý |
|---|---|---|
| `build.gradle.kts` | Hai người thêm dependency cùng lúc | Giữ cả hai dependency, sắp xếp theo alphabet |
| `AndroidManifest.xml` | Thêm permission hoặc activity mới | Giữ tất cả khai báo, không xoá |
| `strings.xml` | Thêm string resource cùng key | Giữ cả hai string (khác key), thống nhất key với nhau |
| `NavGraph.kt` | Thêm destination mới | Giữ tất cả destination, kiểm tra không trùng route |
| File ViewModel | Thêm hàm / state cùng vị trí | Giữ cả hai hàm, tái cấu trúc nếu trùng lặp |

### 8.4 Khi không tự giải quyết được

1. Tạo issue trên GitHub mô tả conflict
2. Tag thành viên liên quan
3. Giải quyết qua call ngắn (15–30 phút) — không để conflict block sprint quá 4 giờ

---

## 9. Quy tắc bổ sung

### 9.1 Quản lý issue

- Mọi công việc phải có issue trên GitHub trước khi tạo nhánh
- Issue phải có label: `feature`, `bug`, `docs`, `chore`, `question`
- Gán issue cho thành viên phụ trách trước khi bắt đầu làm
- Dùng GitHub Project Board để theo dõi trạng thái: `Backlog → In Progress → In Review → Done`

### 9.2 Họp nhóm định kỳ

| Loại họp | Tần suất | Nội dung | Thời lượng |
|---|---|---|---|
| Standup | Hằng ngày (qua chat) | Hôm qua làm gì, hôm nay làm gì, blockers | 5 phút |
| Sprint review | Cuối mỗi sprint | Demo tính năng hoàn thành, demo cho nhau | 30–45 phút |
| Architecture sync | Khi cần | Thảo luận quyết định kỹ thuật lớn | 30 phút |

### 9.3 Không làm

- ❌ Không commit trực tiếp code chưa test lên `develop`
- ❌ Không merge PR của chính mình khi chưa có review (trừ trường hợp khẩn cấp và phải thông báo nhóm)
- ❌ Không xoá nhánh `main`, `develop`
- ❌ Không rebase nhánh `develop` hoặc `main`
- ❌ Không để PR mở quá 3 ngày mà không có activity

### 9.4 Công cụ hỗ trợ

| Công cụ | Mục đích |
|---|---|
| GitHub Issues + Project | Quản lý task, theo dõi tiến độ |
| GitHub Pull Requests | Code review, merge |
| Android Studio | IDE chính |
| ktlint | Lint / format code Kotlin (chạy trước khi commit) |
| Git hooks (tuỳ chọn) | Tự động chạy lint khi commit |

### 9.5 Kiểm tra lint trước khi push

```bash
cd android-app
./gradlew ktlintCheck        # Kiểm tra
./gradlew ktlintFormat       # Tự động sửa format
```

---

## Lịch sử thay đổi tài liệu

| Phiên bản | Ngày | Người thực hiện | Nội dung thay đổi |
|---|---|---|---|
| 1.0 | 03/05/2026 | Nghĩa | Tạo tài liệu quy trình phát triển nhóm |

---

*Tài liệu này là quy trình làm việc chính thức của nhóm. Mọi thành viên có trách nhiệm đọc, hiểu và tuân thủ.*
