# Review Phần Nghĩa — Emotion Friend

> Branch: `chore/review-nghia-system`  
> Ngày review: dựa trên trạng thái sau commit `5977c80` (main HEAD)

---

## Phạm vi phụ trách của Nghĩa

| # | Khu vực |
|---|---------|
| 1 | Setup cấu trúc project Android |
| 2 | Setup GitHub repository |
| 3 | Setup Kotlin + Jetpack Compose |
| 4 | Setup Navigation Compose |
| 5 | Setup Material 3 Theme |
| 6 | Setup project structure (monorepo) |
| 7 | Final Integration |
| 8 | README / Installation Guide |
| 9 | QA checklist tổng thể |
| 10 | CI/CD GitHub Actions |
| 11 | Docker deployment lên VPS |
| 12 | MySQL deployment bằng Docker |
| 13 | Backend deployment bằng Docker |
| 14 | Final build APK |

---

## Đã hoàn thành ✅

### 1. Setup cấu trúc project Android
- `android-app/settings.gradle.kts` — single-module `:app`, repo config
- `android-app/build.gradle.kts` — root plugin declarations (apply false)
- `android-app/gradle/libs.versions.toml` — version catalog đầy đủ (Kotlin 2.0.0, Compose BOM 2024.08.00, Hilt 2.51.1, Room 2.6.1, CameraX 1.3.4, WorkManager 2.9.1, Ktor 2.3.12, detekt 1.23.6, coroutines-test 1.8.1)
- `android-app/app/build.gradle.kts` — namespace, compileSdk 35, minSdk 26, targetSdk 35, detekt block, tất cả dependencies
- Commit: `ab0e164` (baseline), hoàn thiện qua `3818cb6`, `fad3394`, `3b60ad7`

### 2. Setup GitHub repository
- `.github/CODEOWNERS` — Nghĩa là owner toàn bộ `android-app/`, `backend-api/`, `infra/`, `docs/`, `.github/workflows/`
- `.github/pull_request_template.md` — template đầy đủ: mô tả, scope checklist, screenshots, test notes
- `.github/workflows/pr-validation.yml` — branch name convention + PR title (Conventional Commits) validation
- Commit: `f6b14e4` (CI: add CODEOWNERS and PR validation)

### 3. Setup Kotlin + Jetpack Compose
- Kotlin 2.0.0 với `kotlin.compose` plugin (Compose Compiler Gradle plugin)
- KSP 2.0.0-1.0.21 thay thế kapt
- Compose BOM 2024.08.00 pin toàn bộ Compose libraries
- `kotlinOptions.jvmTarget = "17"`, `compileOptions` SOURCE/TARGET_17

### 4. Setup Navigation Compose
- `core/navigation/AppRoute.kt` — sealed class, 7 routes: Home, LearnEmotion, Situation, ExpressCamera, Relax, Journal, Progress
- `core/navigation/EmotionFriendNavHost.kt` — NavHost với `startDestination = Home`, tất cả 7 routes wired, feature screens chỉ nhận callbacks (không inject NavController)
- Commit: `2ece7a8`

### 5. Setup Material 3 Theme
- `core/designsystem/theme/Color.kt` — palette đầy đủ: SkyBlue, MintGreen, WarmCream, emotion colors (Happy/Sad/Angry/Surprised/Calm/Tired + backgrounds), fixed `EmotionTired`/`EmotionTiredBg` (commit `b57ffae`)
- `core/designsystem/theme/Type.kt` — child-friendly Typography, sizes 16–40sp
- `core/designsystem/theme/Shape.kt` — rounded shapes (8–50dp)
- `core/designsystem/theme/Theme.kt` — `EmotionFriendTheme` với `lightColorScheme`, dynamic color disabled (emotion colors phải stable)
- `res/values/themes.xml` — XML theme cho splash window background
- `res/values/strings.xml` — app name, labels, common actions, feedback strings

### 6. Setup project structure (monorepo)
- Monorepo layout: `android-app/`, `backend-api/`, `infra/`, `docs/`, `.github/`
- `android-app/app/src/main/java/com/emotionfriend/` — package hierarchy: `core/`, `data/`, `domain/`, `feature/`
- `core/common/` — placeholder (`.gitkeep`), reserved for shared utilities
- Seed assets: `assets/seed/emotion_cards.json` (7 entries), `assets/seed/scenario_lessons.json`
- Commit: `98ff838` (initialize monorepo structure)

### 7. Final Integration
- `EmotionFriendApplication.kt` — `@HiltAndroidApp`, injects + calls `SeedDataInitializer`
- `MainActivity.kt` — `@AndroidEntryPoint`, `enableEdgeToEdge()`, `EmotionFriendTheme { EmotionFriendNavHost() }`
- `AndroidManifest.xml` — `CAMERA`, `INTERNET` permissions; `.EmotionFriendApplication` registered
- 5 DI modules: `AppModule` (ApplicationScope), `DatabaseModule` (Room + 4 DAOs), `DataStoreModule`, `RemoteModule` (Ktor HttpClient), `RepositoryModule` (5 @Binds)
- `core/designsystem/components/` — 6 shared components: EmotionCard, EmotionOptionButton, EmotionPrimaryButton, EmotionScreenScaffold, FeedbackBanner, ProgressPill
- Commit: `b57ffae` (integrate app flows for P6 demo)

### 8. README / Installation Guide
- `README.md` — full rewrite (commit `5977c80`): tech stack table, quick start, architecture diagram, team roles, CI/CD badge, API docs
- `docs/BACKEND_SETUP.md` — local setup với Docker (port 8081), env vars table đầy đủ (DATABASE_USER, DATABASE_PASSWORD included, fixed `963c38b`)
- `docs/LOCAL_ENVIRONMENT.md` — môi trường dev local
- `docs/DEVELOPMENT_WORKFLOW.md` — quy trình làm việc nhóm
- `docs/PROJECT_SCOPE.md` — phạm vi dự án

### 9. QA checklist tổng thể
- `docs/QA_CHECKLIST.md` — 77 mục, 12 sections: Install/Launch, Navigation, Learn, Situation, Express, Relax, Journal, Progress, Backend, Network, Performance, Accessibility
- Commit: `519854e` (tag `v0.7.0`)

### 10. CI/CD GitHub Actions
- `.github/workflows/android-ci.yml` — trigger: push/PR to main/develop; steps: checkout → JDK 17 → Gradle cache → detekt (ignoreFailures=true, upload report) → testDebugUnitTest → assembleDebug → upload APK artifact (14 days)
- `.github/workflows/backend-ci.yml` — trigger: push/PR to main/develop (path: backend-api/**); steps: checkout → JDK 21 → Gradle cache → test → build
- `.github/workflows/pr-validation.yml` — branch name regex check + PR title Conventional Commits check
- Gradle cache key: hash of `*.gradle*` + `libs.versions.toml`
- Commits: `b7d5872`, `3700791`, `f6b14e4`

### 12. MySQL deployment bằng Docker
- `infra/docker-compose.yml` — MySQL 8.4, healthcheck (`mysqladmin ping`), persistent volume `mysql_data`, port `3307:3306` (tránh conflict local MySQL)
- `infra/.env.example` — placeholder creds template
- `infra/env/mysql.env.example` — MySQL-specific template
- `infra/.env` — gitignored (real credentials stays local)
- Commit: `26ff7f5`

### 13. Backend deployment bằng Docker
- `infra/docker-compose.yml` — `backend-api` service, `depends_on: mysql (condition: service_healthy)`, port `8081:8080`, healthcheck (`wget /health`)
- `backend-api/Dockerfile` — multi-stage build (builder + runtime), `sed` strips Windows `org.gradle.java.home` path để build thành công trên Linux container
- `infra/env/backend.env.example` — backend env template
- Commit: `963c38b`

### 14. Final build APK (Debug)
- `android-ci.yml` step 8: `./gradlew assembleDebug --no-daemon`
- `android-ci.yml` step 9: upload `app-debug.apk` artifact, retention 14 ngày
- `docs/BUILD_AND_RELEASE.md` — 6 sections: build local, find APK, GitHub Actions artifact, install on device, pre-demo checklist, Firebase App Distribution plan
- Commit: `a10c21d`, `b7d5872`

---

## Chưa hoàn thành / Còn thiếu ⚠️

### 11. Docker deployment lên VPS ❌ (Thiếu hoàn toàn)
Hiện tại chỉ có `docker-compose.yml` cho **local development**. Chưa có:

| Thiếu | Mô tả |
|-------|-------|
| `infra/docker-compose.prod.yml` | Override config cho VPS: remove build context, dùng image tag, thêm resource limits |
| `infra/nginx.conf` | Reverse proxy template: `proxy_pass http://backend-api:8080`, SSL termination placeholder |
| `.github/workflows/deploy.yml` | CD workflow: SSH vào VPS → `git pull` → `docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d` |
| GitHub Secrets documentation | `VPS_HOST`, `VPS_USER`, `VPS_SSH_KEY` — chưa có hướng dẫn setup |
| `docs/VPS_DEPLOYMENT.md` | Hướng dẫn deploy lên VPS từng bước |

### 14. Final build APK (Release signed) ⚠️ (Chỉ có debug)
| Thiếu | Mô tả |
|-------|-------|
| `signingConfigs {}` block trong `app/build.gradle.kts` | Cấu hình ký APK cho release build |
| `.github/workflows/release.yml` | Workflow tự động ký và upload signed APK từ GitHub Secrets |
| Keystore creation guide trong `BUILD_AND_RELEASE.md` | Chưa có hướng dẫn tạo `release.keystore` |

### Minor bugs ⚠️
| File | Vấn đề | Sửa |
|------|--------|-----|
| `docs/BUILD_AND_RELEASE.md` | `git pull origin master` → repo dùng branch `main` | Đổi thành `git pull origin main` |

---

## File cần tạo / sửa

| Priority | Action | File | Mô tả |
|----------|--------|------|-------|
| 🔴 Critical | Tạo | `infra/docker-compose.prod.yml` | Production override: image tag, resource limits, no build context |
| 🔴 Critical | Tạo | `infra/nginx.conf` | Reverse proxy template cho VPS |
| 🔴 Critical | Tạo | `.github/workflows/deploy.yml` | CD workflow SSH deploy to VPS |
| 🟡 Important | Sửa | `android-app/app/build.gradle.kts` | Thêm `signingConfigs {}` block cho release |
| 🟡 Important | Tạo | `.github/workflows/release.yml` | Signed release APK workflow |
| 🟡 Important | Sửa | `docs/BUILD_AND_RELEASE.md` | Fix `master` → `main`; thêm signing guide |
| 🟢 Optional | Tạo | `docs/VPS_DEPLOYMENT.md` | Hướng dẫn deploy VPS chi tiết (SSH key, firewall, nginx install) |

---

## Thứ tự xử lý

```
[PROMPT 2] Docker VPS deployment
  ├── Tạo infra/docker-compose.prod.yml
  ├── Tạo infra/nginx.conf
  ├── Tạo .github/workflows/deploy.yml
  └── Tạo docs/VPS_DEPLOYMENT.md

[PROMPT 3] Release APK signing
  ├── Thêm signingConfigs vào app/build.gradle.kts
  ├── Tạo .github/workflows/release.yml
  └── Cập nhật docs/BUILD_AND_RELEASE.md (master→main + signing guide)
```

---

## Tổng kết

| Khu vực | Trạng thái |
|---------|-----------|
| Setup cấu trúc project Android | ✅ Hoàn thành |
| Setup GitHub repository | ✅ Hoàn thành |
| Setup Kotlin + Jetpack Compose | ✅ Hoàn thành |
| Setup Navigation Compose | ✅ Hoàn thành |
| Setup Material 3 Theme | ✅ Hoàn thành |
| Setup project structure | ✅ Hoàn thành |
| Final Integration | ✅ Hoàn thành |
| README / Installation Guide | ✅ Hoàn thành |
| QA checklist tổng thể | ✅ Hoàn thành |
| CI/CD GitHub Actions | ✅ Hoàn thành |
| Docker deployment lên VPS | ❌ Chưa có |
| MySQL deployment bằng Docker | ✅ Hoàn thành |
| Backend deployment bằng Docker | ✅ Hoàn thành |
| Final build APK (debug) | ✅ Hoàn thành |
| Final build APK (release signed) | ⚠️ Chưa có |

**12/14 hoàn thành.** Còn lại: VPS deployment (❌) và release signing (⚠️).
