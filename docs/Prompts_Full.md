# Emotion Friend – Full Prompt Pack cho Nghĩa

> Toàn bộ các task còn 0% được quy về cho Nghĩa thực hiện.
>
> Mục tiêu:
> - Hoàn thiện toàn bộ phần coding + devops còn thiếu.
> - Đảm bảo app build được.
> - Có thể demo đầy đủ 3 tác vụ chính.
> - Có backend/API cơ bản.
> - Có CI/CD và Docker.
> - Có Parent Dashboard, Progress, Profile.
> - Có sync local/backend.
> - Có thể nộp source code cuối kỳ.

Stack:
- Android Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Room
- Retrofit/Ktor Client
- Ktor Backend
- MySQL
- Docker
- GitHub Actions

---

# PROMPT 1 — Audit toàn bộ project và tạo roadmap sửa lỗi

```text
Bạn là Senior Android Architect + Technical Lead.

Hãy rà soát toàn bộ project Emotion Friend hiện tại.

Bối cảnh:
Emotion Friend là ứng dụng mobile hỗ trợ trẻ tự kỷ học nhận biết, hiểu, biểu đạt và điều hòa cảm xúc.

Các màn hình chính:
- Home
- Learn Emotion
- Situation
- Camera Practice
- Relax
- My Emotion
- Progress
- Parent Dashboard
- Profile

Yêu cầu:
1. Kiểm tra toàn bộ cấu trúc Android project.
2. Kiểm tra Gradle build.
3. Kiểm tra package structure.
4. Kiểm tra navigation.
5. Kiểm tra state flow/viewmodel.
6. Kiểm tra backend Ktor.
7. Kiểm tra database MySQL.
8. Kiểm tra Docker setup.
9. Kiểm tra GitHub Actions nếu có.

Sau đó:
- Liệt kê toàn bộ lỗi.
- Liệt kê phần chưa hoàn thiện.
- Gán mức độ:
  + Critical
  + High
  + Medium
  + Low
- Tạo roadmap sửa theo thứ tự ưu tiên.

Output:
- Checklist chi tiết.
- File cần sửa.
- Những phần nên mock để kịp deadline.
- Những phần phải làm thật.
```

---

# PROMPT 2 — Chuẩn hóa kiến trúc Android + Navigation + Design System

```text
Bạn là Android Architect dùng Kotlin + Jetpack Compose.

Hãy chuẩn hóa kiến trúc Android project Emotion Friend.

Yêu cầu:
1. Tổ chức package:
- core/designsystem
- core/navigation
- core/network
- core/database
- core/common
- feature/home
- feature/learn
- feature/situation
- feature/camera
- feature/relax
- feature/emotionlog
- feature/progress
- feature/parent
- feature/profile

2. Tạo AppNavigation.
3. Tạo route constants.
4. Chuẩn hóa MainActivity.
5. Chuẩn hóa Material 3 theme.
6. Tạo reusable components:
- EmotionFriendButton
- EmotionCard
- FeatureMenuCard
- FeedbackDialog
- ProgressSummaryCard
- ScreenHeader

7. UI phải theo hướng Calm Game-based Learning:
- màu dịu
- icon lớn
- spacing rộng
- ít chữ
- feedback thân thiện

Output:
- Code hoàn chỉnh.
- File thêm/sửa.
- Cách test navigation.
- Cách kiểm tra UI consistency.
```

---

# PROMPT 3 — Hoàn thiện 3 tác vụ chính end-to-end

```text
Bạn là Android Kotlin Developer.

Hãy hoàn thiện 3 tác vụ chính chạy được end-to-end cho Emotion Friend.

Tác vụ 1 — Learn Emotion:
- Hiển thị flashcard.
- Chọn cảm xúc.
- Feedback đúng/sai.
- Chuyển bài.
- Lưu kết quả.

Tác vụ 2 — Situation:
- Hiển thị tình huống.
- Chọn cảm xúc.
- Giải thích đáp án.
- Chuyển bài.
- Lưu kết quả.

Tác vụ 3 — Camera Practice:
- Camera preview hoặc mock camera.
- Yêu cầu biểu cảm.
- Nút kiểm tra.
- Feedback AI mock.
- Lưu kết quả.

Yêu cầu:
- Kotlin + Compose.
- ViewModel + StateFlow.
- Không crash khi dữ liệu rỗng.
- Dùng Room nếu đã có.
- Nếu backend chưa ổn thì dùng local repository.

Output:
- Screen.
- ViewModel.
- State.
- Repository.
- Navigation.
- Cách test.
```

---

# PROMPT 4 — Hoàn thiện Room Database + Local First Architecture

```text
Bạn là Android Data Engineer.

Hãy triển khai local-first architecture cho Emotion Friend.

Dữ liệu cần lưu:
- Emotion
- Situation
- ExerciseResult
- EmotionLog
- ProgressSummary

Yêu cầu:
1. Tạo Room Entity.
2. Tạo DAO.
3. Tạo AppDatabase.
4. Tạo Repository.
5. Seed data cho Emotion và Situation.
6. ExerciseResult và EmotionLog phải có field isSynced.
7. App vẫn hoạt động khi backend chết.
8. Dùng Flow/StateFlow.

Output:
- Entity.
- DAO.
- Database.
- Repository.
- Seed data.
- Cách test local database.
```

---

# PROMPT 5 — Backend Ktor + MySQL API đầy đủ

```text
Bạn là Backend Developer dùng Kotlin Ktor + MySQL.

Hãy hoàn thiện backend API cho Emotion Friend.

API cần có:
1. GET /api/emotions
2. GET /api/situations
3. POST /api/exercise-results
4. GET /api/progress/{userId}
5. GET /api/progress/{userId}/history
6. POST /api/emotion-logs
7. GET /api/emotion-logs/{userId}
8. POST /api/expression-practice/result
9. GET /api/health

Yêu cầu:
- Response JSON rõ ràng.
- DTO riêng.
- Error handling cơ bản.
- Seed data.
- Mock AI result cho expression practice.
- Kết nối MySQL qua env.

Output:
- Route.
- DTO.
- Service.
- Repository.
- SQL schema.
- SQL seed.
- curl test.
```

---

# PROMPT 6 — Đồng bộ Room ↔ Backend ↔ MySQL

```text
Bạn là Android Architect.

Hãy triển khai cơ chế sync dữ liệu giữa Android Room và Backend API.

Luồng:
1. Save local trước.
2. Call backend sau.
3. Nếu thành công:
- mark isSynced = true
4. Nếu thất bại:
- giữ pending

Dữ liệu sync:
- ExerciseResult
- EmotionLog
- ProgressSummary

Yêu cầu:
1. DAO query pending data.
2. syncPendingData().
3. Retry cơ bản.
4. App không crash nếu backend không chạy.
5. Có debug log.
6. Có thể trigger sync khi mở app.

Output:
- Sync flow.
- DAO query.
- Repository sync.
- API client.
- ViewModel.
- Cách test offline/online.
```

---

# PROMPT 7 — Hoàn thiện Parent Dashboard + Progress + Profile

```text
Bạn là Android Kotlin Developer.

Hãy hoàn thiện:
1. Parent Dashboard
2. Progress Screen
3. Emotion Log History
4. Parent Report Detail
5. Profile Screen

Yêu cầu Parent Dashboard:
- Tên trẻ.
- Tổng quan tiến trình.
- Tỷ lệ đúng.
- Emotion hay sai.
- Shortcut sang progress/history.

Yêu cầu Progress:
- Số bài hoàn thành.
- Accuracy rate.
- Recent exercises.
- Chart/progress bar.

Yêu cầu Emotion Log:
- Danh sách cảm xúc.
- Note.
- Time.

Yêu cầu Profile:
- Họ tên.
- Email.
- Vai trò.
- Tên trẻ.
- Edit local/mock.

Yêu cầu kỹ thuật:
- Compose + ViewModel.
- Lấy data từ Room/repository.
- Không overload UI.

Output:
- Screen.
- ViewModel.
- Repository.
- Navigation.
- Cách test.
```

---

# PROMPT 8 — Docker hóa Backend + MySQL

```text
Bạn là DevOps Engineer.

Hãy Docker hóa backend Ktor + MySQL cho Emotion Friend.

Yêu cầu:
1. Dockerfile backend.
2. docker-compose.yml:
- backend
- mysql
- network
- mysql volume

3. .env.example:
- DB_HOST
- DB_PORT
- DB_NAME
- DB_USER
- DB_PASSWORD
- SERVER_PORT

4. Backend đọc config từ env.
5. Có health check.
6. Có SQL init.
7. Không hardcode secret.

Output:
- Dockerfile.
- docker-compose.yml.
- .env.example.
- README deploy.
- Lệnh test backend.
```

---

# PROMPT 9 — GitHub Actions CI/CD Build APK + Backend

```text
Bạn là DevOps Engineer.

Hãy tạo GitHub Actions CI/CD cho Emotion Friend.

Android workflow:
1. Trigger:
- push
- pull request

2. Steps:
- checkout
- setup JDK
- gradle cache
- ./gradlew clean
- ./gradlew test
- ./gradlew assembleDebug
- upload APK artifact

Backend workflow:
1. Build Ktor backend.
2. Run test nếu có.
3. Build Docker image.

Yêu cầu:
- Tạo:
  + android-build.yml
  + backend-build.yml
- Workflow rõ ràng.
- Dễ debug.
- Hỗ trợ mono repo.

Output:
- YAML hoàn chỉnh.
- Cách tải APK artifact.
- Cách kiểm tra CI.
```

---

# PROMPT 10 — Final QA + Release Checklist

```text
Bạn là Technical Lead chuẩn bị release project Emotion Friend.

Hãy thực hiện final QA cho toàn bộ project.

Kiểm tra:
1. Android build.
2. APK chạy.
3. Navigation.
4. 3 tác vụ chính.
5. Room database.
6. API backend.
7. Sync Room/backend.
8. Docker backend.
9. GitHub Actions.
10. UI consistency.
11. Crash cases.
12. Empty states.
13. Loading states.
14. Offline mode.

Sau đó:
- Tạo final release checklist.
- Liệt kê bug còn tồn tại.
- Gợi ý phần có thể mock.
- Gợi ý phần nên cắt scope.
- Liệt kê risk trước demo.

Output:
- Final QA checklist.
- Bug list.
- Risk analysis.
- Demo checklist.
- Final release steps.
```

---

# THỨ TỰ CHẠY PROMPT KHUYẾN NGHỊ

1. Prompt 1 — Audit project.
2. Prompt 2 — Architecture + Design System.
3. Prompt 4 — Room Database.
4. Prompt 3 — 3 tác vụ chính.
5. Prompt 5 — Backend API.
6. Prompt 6 — Sync local/backend.
7. Prompt 7 — Parent + Progress + Profile.
8. Prompt 8 — Docker.
9. Prompt 9 — GitHub Actions.
10. Prompt 10 — Final QA.

---

# RULE QUAN TRỌNG

Nếu thiếu thời gian:

BẮT BUỘC GIỮ:
- Android build được.
- 3 tác vụ chính chạy được.
- APK demo được.
- UI ổn định.
- Progress hoạt động.

CÓ THỂ MOCK:
- AI thật.
- Backend advanced.
- Retry sync phức tạp.
- Notification.
- Admin system.

KHÔNG NÊN ÔM:
- Production deploy.
- Auth phức tạp.
- Real AI.
- Full admin CMS.

