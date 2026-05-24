-- =============================================================================
-- Emotion Friend — MySQL Schema
-- =============================================================================
-- Database : emotion_friend
-- Charset  : utf8mb4 (full Unicode, emoji support)
-- Engine   : InnoDB (foreign key support)
--
-- MVP tables  : users, emotions, situations, exercise_results,
--               emotion_logs, progress, settings
-- Non-MVP     : notifications  (scaffold only, not wired in MVP)
-- =============================================================================

CREATE DATABASE IF NOT EXISTS emotion_friend
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE emotion_friend;

-- =============================================================================
-- 1. users
-- =============================================================================
-- Stores child user profiles.
-- MVP: single-user local profile; user_id = 1 used by default in the app.
-- Future: multi-user / parent-child accounts.
-- =============================================================================
CREATE TABLE users (
    id            BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT,
    name          VARCHAR(100)     NOT NULL DEFAULT '',
    email         VARCHAR(254)     NOT NULL,
    display_name  VARCHAR(100)     NOT NULL DEFAULT '',
    password_hash VARCHAR(64)      NOT NULL DEFAULT '' COMMENT 'SHA-256 hex; migrate to bcrypt in production',
    role          ENUM('CHILD','PARENT','THERAPIST') NOT NULL DEFAULT 'CHILD',
    is_verified   TINYINT(1)       NOT NULL DEFAULT 0,
    age           TINYINT UNSIGNED,
    avatar_url    VARCHAR(255),
    created_at    TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='User accounts. Supports CHILD/PARENT/THERAPIST roles.';


-- =============================================================================
-- 2. emotions  [MASTER DATA — MVP]
-- =============================================================================
-- Reference list of 6 core emotions shown in the app.
-- Populated once via seed data; not expected to change at runtime.
-- =============================================================================
CREATE TABLE emotions (
    id           INT UNSIGNED NOT NULL AUTO_INCREMENT,
    name         VARCHAR(50)  NOT NULL COMMENT 'Machine key, e.g. happy',
    display_name VARCHAR(100) NOT NULL COMMENT 'Localised label shown in UI',
    emoji        VARCHAR(10)  NOT NULL COMMENT 'Single emoji character',
    color_hex    CHAR(7)      NOT NULL COMMENT '#RRGGBB matching app theme token',
    description  TEXT,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_emotion_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Master list of 6 core emotions. Read-only after seeding.';


-- =============================================================================
-- 3. situations  [MASTER DATA — MVP]
-- =============================================================================
-- Scenario cards presented in the Learn Emotion / Situation screens.
-- Each situation maps to a target emotion the child should recognise.
-- =============================================================================
CREATE TABLE situations (
    id               INT UNSIGNED NOT NULL AUTO_INCREMENT,
    title            VARCHAR(200) NOT NULL,
    description      TEXT,
    emotion_id       INT UNSIGNED NOT NULL  COMMENT 'Correct emotion for this scenario',
    image_url        VARCHAR(255)           COMMENT 'Local drawable path or remote URL',
    difficulty_level ENUM('easy','medium','hard') NOT NULL DEFAULT 'easy',
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_situation_emotion
        FOREIGN KEY (emotion_id) REFERENCES emotions(id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Scenario cards. difficulty_level drives progression logic in future.';


-- =============================================================================
-- 4. exercise_results  [MVP]
-- =============================================================================
-- Records every answer the child gives during exercises.
-- Used to calculate progress stats (correct rate, streak, etc.).
-- =============================================================================
CREATE TABLE exercise_results (
    id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id             BIGINT UNSIGNED NOT NULL,
    situation_id        INT UNSIGNED    NOT NULL,
    selected_emotion_id INT UNSIGNED    NOT NULL  COMMENT 'What the child tapped',
    correct_emotion_id  INT UNSIGNED    NOT NULL  COMMENT 'What the right answer was',
    is_correct          TINYINT(1)      NOT NULL DEFAULT 0,
    response_time_ms    INT UNSIGNED             COMMENT 'Milliseconds taken to answer',
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_result_user
        FOREIGN KEY (user_id)             REFERENCES users(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_result_situation
        FOREIGN KEY (situation_id)        REFERENCES situations(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_result_selected_emotion
        FOREIGN KEY (selected_emotion_id) REFERENCES emotions(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_result_correct_emotion
        FOREIGN KEY (correct_emotion_id)  REFERENCES emotions(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,

    INDEX idx_result_user_created (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='One row per answered exercise. Source of truth for progress calculation.';


-- =============================================================================
-- 5. emotion_logs  [MVP]
-- =============================================================================
-- Daily emotion diary — child taps how they feel today.
-- Feeds the "My Emotion / Journal" screen (Toàn's feature); this table is the
-- shared data contract between backend and Room.
-- =============================================================================
CREATE TABLE emotion_logs (
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id    BIGINT UNSIGNED NOT NULL,
    emotion_id INT UNSIGNED    NOT NULL,
    note       TEXT                     COMMENT 'Optional free-text note by caregiver',
    logged_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
                                        COMMENT 'Actual timestamp of the log entry',
    created_at TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_log_user
        FOREIGN KEY (user_id)    REFERENCES users(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_log_emotion
        FOREIGN KEY (emotion_id) REFERENCES emotions(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,

    INDEX idx_log_user_date (user_id, logged_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Emotion diary entries. One row = one check-in. Synced from Room (Toàn).';


-- =============================================================================
-- 6. progress  [MVP]
-- =============================================================================
-- Aggregated stats per user — updated after each exercise session.
-- One row per user (UNIQUE constraint).
-- =============================================================================
CREATE TABLE progress (
    id                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id            BIGINT UNSIGNED NOT NULL,
    total_exercises    INT UNSIGNED    NOT NULL DEFAULT 0,
    correct_answers    INT UNSIGNED    NOT NULL DEFAULT 0,
    current_streak     INT UNSIGNED    NOT NULL DEFAULT 0
                                                COMMENT 'Consecutive correct-answer days',
    longest_streak     INT UNSIGNED    NOT NULL DEFAULT 0,
    last_activity_date DATE                     COMMENT 'Date of most recent exercise',
    updated_at         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_progress_user (user_id),
    CONSTRAINT fk_progress_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Aggregated per-user progress. Recomputed from exercise_results on sync.';


-- =============================================================================
-- 7. notifications  [NON-MVP — scaffold only]
-- =============================================================================
-- Push / local notification records. Table exists for schema completeness;
-- NOT wired to any MVP feature. Activate in a later sprint.
-- =============================================================================
CREATE TABLE notifications (
    id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id      BIGINT UNSIGNED NOT NULL,
    title        VARCHAR(200)    NOT NULL,
    message      TEXT,
    type         ENUM('reminder','achievement','system') NOT NULL DEFAULT 'reminder',
    is_read      TINYINT(1)      NOT NULL DEFAULT 0,
    scheduled_at TIMESTAMP                COMMENT 'NULL = send immediately',
    created_at   TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON UPDATE CASCADE ON DELETE CASCADE,

    INDEX idx_notif_user_read (user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='[NON-MVP] Notification inbox. Not used in first release.';


-- =============================================================================
-- 8. settings  [MVP]
-- =============================================================================
-- Per-user app settings. One row per user (UNIQUE constraint).
-- =============================================================================
CREATE TABLE settings (
    id                   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id              BIGINT UNSIGNED NOT NULL,
    sound_enabled        TINYINT(1)      NOT NULL DEFAULT 1,
    notification_enabled TINYINT(1)      NOT NULL DEFAULT 1,
    reminder_time        TIME            NOT NULL DEFAULT '19:00:00'
                                                  COMMENT 'Daily practice reminder time',
    language             VARCHAR(10)     NOT NULL DEFAULT 'vi',
    created_at           TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_settings_user (user_id),
    CONSTRAINT fk_settings_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Per-user app settings. Row auto-created when new user registers.';


-- =============================================================================
-- SEED DATA
-- =============================================================================

-- ── 1. Default users (demo accounts for dev/QA) ──────────────────────────────
-- Passwords are SHA-256 of the plain-text values shown in the comment.
INSERT INTO users (id, name, email, display_name, password_hash, role, is_verified, age) VALUES
    (1, 'Bé Minh',    'child@emotionfriend.app',     'Minh Tuấn',  'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'CHILD',     1, 8),
    (2, 'Nguyễn Lan', 'parent@emotionfriend.app',    'Nguyễn Lan', '2b3a9b6a77fbbc0fd3cf9f0e55a9f0f3a3a1e74a0fbc8f9d2e4c9b0f4e92d5f', 'PARENT',    1, NULL),
    (3, 'BS. Hoa',    'therapist@emotionfriend.app', 'BS. Hoa',    'e6b6e6a5ced1b2e9c4c02be7cc7a5fd48a4a9ea1ea82b5f4e34e58f9c3e2d3a2', 'THERAPIST', 1, NULL);

-- ── 2. Emotions (6 core — matches Color.kt tokens) ───────────────────────────
INSERT INTO emotions (id, name, display_name, emoji, color_hex, description) VALUES
    (1, 'happy',     'Vui',       '😄', '#FFD600', 'Cảm giác hạnh phúc, vui vẻ'),
    (2, 'sad',       'Buồn',      '😢', '#42A5F5', 'Cảm giác buồn bã, không vui'),
    (3, 'angry',     'Tức giận',  '😠', '#EF5350', 'Cảm giác tức giận, bực bội'),
    (4, 'tired',     'Mệt mỏi',   '😴', '#AB47BC', 'Cảm giác mệt mỏi, buồn ngủ'),
    (5, 'surprised', 'Bất ngờ',   '😲', '#FF7043', 'Cảm giác ngạc nhiên, bất ngờ'),
    (6, 'calm',      'Bình tĩnh', '😌', '#66BB6A', 'Cảm giác bình tĩnh, thư giãn');

-- ── 3. Situations (15 easy scenarios for MVP demo) ───────────────────────────
INSERT INTO situations (id, title, description, emotion_id, image_url, difficulty_level) VALUES
    -- Happy
    (1,  'Được tặng quà sinh nhật',
         'Hôm nay là sinh nhật của bé. Mọi người đem bánh kem và quà đến tặng.', 1, NULL, 'easy'),
    (2,  'Chơi đu quay cùng bạn',
         'Bé và người bạn thân cùng chơi ở công viên.', 1, NULL, 'easy'),
    (3,  'Được khen trước lớp',
         'Cô giáo khen bé đọc bài thật tốt trước cả lớp.', 1, NULL, 'easy'),

    -- Sad
    (4,  'Mất đồ chơi yêu thích',
         'Bé không tìm thấy chú gấu bông đã chơi từ nhỏ.', 2, NULL, 'easy'),
    (5,  'Bạn bè không chịu chơi cùng',
         'Các bạn trong nhóm nói bé không được chơi cùng.', 2, NULL, 'easy'),
    (6,  'Chia tay ông bà về quê',
         'Ông bà phải lên xe về quê, bé vẫy tay chào.', 2, NULL, 'easy'),

    -- Angry
    (7,  'Em lấy đồ chơi không hỏi',
         'Em trai lấy xe đồ chơi của bé mà không xin phép.', 3, NULL, 'easy'),
    (8,  'Bị đổ lỗi oan',
         'Bạn bè nói bé đánh vỡ bình hoa nhưng thật ra không phải bé.', 3, NULL, 'easy'),

    -- Tired
    (9,  'Học bài đến khuya',
         'Bé phải ngồi học bài rất lâu, mắt díp lại.', 4, NULL, 'easy'),
    (10, 'Đi bộ về nhà dưới nắng',
         'Trời nóng, bé đi bộ xa về nhà sau buổi học.', 4, NULL, 'easy'),

    -- Surprised
    (11, 'Bạn bè tổ chức tiệc bất ngờ',
         'Bé mở cửa phòng và thấy mọi người hô "Bất ngờ!".', 5, NULL, 'easy'),
    (12, 'Thấy một con bướm lạ',
         'Bé thấy một con bướm với màu sắc chưa bao giờ thấy.', 5, NULL, 'easy'),

    -- Calm
    (13, 'Ngồi nghe nhạc trong phòng',
         'Phòng yên tĩnh, bé ngồi nghe nhạc nhẹ.', 6, NULL, 'easy'),
    (14, 'Tô màu tranh cùng mẹ',
         'Bé và mẹ cùng tô màu một bức tranh dễ thương.', 6, NULL, 'easy'),
    (15, 'Đọc sách trước khi ngủ',
         'Bé nằm đọc cuốn truyện tranh yêu thích.', 6, NULL, 'easy');

-- ── 4. Default settings for user 1 ───────────────────────────────────────────
INSERT INTO settings (user_id, sound_enabled, notification_enabled, reminder_time, language)
VALUES (1, 1, 1, '19:00:00', 'vi');

-- ── 5. Initial progress row for user 1 ───────────────────────────────────────
INSERT INTO progress (user_id, total_exercises, correct_answers, current_streak, longest_streak)
VALUES (1, 0, 0, 0, 0);

-- =============================================================================
-- END
-- =============================================================================
