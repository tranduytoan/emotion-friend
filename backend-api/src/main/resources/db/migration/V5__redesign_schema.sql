-- V5: Redesign schema — proper numeric AUTO_INCREMENT PKs, remove string IDs, fix charset
-- Scenario options use EmotionType codes (HAPPY/SAD/ANGRY/SURPRISED/CALM/TIRED) instead of display text.
-- Seeded data từ V1–V4 bị xoá, chỉ giữ lại emotion_cards vì đây là catalog cố định.

-- ── Drop all old tables (order: dependents first) ────────────────────────────
DROP TABLE IF EXISTS practice_attempts;
DROP TABLE IF EXISTS journal_entries;
DROP TABLE IF EXISTS stories;
DROP TABLE IF EXISTS music_tracks;
DROP TABLE IF EXISTS scenario_lessons;
DROP TABLE IF EXISTS emotion_cards;
DROP TABLE IF EXISTS children;

-- ── 1. children ──────────────────────────────────────────────────────────────
-- id: INT AUTO_INCREMENT thay cho VARCHAR "default_child"
CREATE TABLE children (
    id           INT UNSIGNED  NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100)  NOT NULL,
    age          TINYINT UNSIGNED,
    avatar_emoji VARCHAR(10)   DEFAULT '🧒',
    created_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── 2. emotion_cards ─────────────────────────────────────────────────────────
-- id: TINYINT AUTO_INCREMENT, emotion_type UNIQUE (ứng với enum EmotionType)
CREATE TABLE emotion_cards (
    id           TINYINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    emotion_type VARCHAR(50)      NOT NULL UNIQUE,
    emoji        VARCHAR(10)      NOT NULL,
    label        VARCHAR(100)     NOT NULL,
    description  TEXT             NOT NULL,
    sort_order   TINYINT UNSIGNED NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seed 6 cảm xúc cơ bản — đây là catalog cố định của app, không phải dữ liệu fake
INSERT INTO emotion_cards (emotion_type, emoji, label, description, sort_order) VALUES
  ('HAPPY',     '😄', 'Vui vẻ',    'Cảm giác vui vẻ, hạnh phúc khi có điều tốt xảy ra.',                 1),
  ('SAD',       '😢', 'Buồn',      'Cảm giác buồn bã khi mất đi thứ mình yêu thích hoặc nhớ ai đó.',     2),
  ('ANGRY',     '😠', 'Tức giận',  'Cảm giác tức giận khi có điều gì đó không công bằng.',                3),
  ('SURPRISED', '😲', 'Ngạc nhiên','Cảm giác bất ngờ khi có điều gì đó xảy ra ngoài dự đoán.',           4),
  ('CALM',      '😌', 'Bình tĩnh', 'Cảm giác yên lặng, thư thái, không lo lắng hay bồn chồn.',            5),
  ('TIRED',     '😴', 'Mệt mỏi',   'Cảm giác mệt mỏi, muốn nghỉ ngơi sau khi làm việc hoặc chơi nhiều.', 6);

-- ── 3. scenario_lessons ──────────────────────────────────────────────────────
-- id: INT AUTO_INCREMENT.
-- options: JSON array of EmotionType codes, vd ["HAPPY","ANGRY","CALM","SURPRISED"]
-- correct_emotion: EmotionType code của đáp án đúng (thay cho correct_index)
CREATE TABLE scenario_lessons (
    id              INT UNSIGNED  NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(200)  NOT NULL,
    situation       TEXT          NOT NULL,
    options         JSON          NOT NULL,
    correct_emotion VARCHAR(50)   NOT NULL,
    explanation     TEXT          NOT NULL,
    sort_order      SMALLINT UNSIGNED NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── 4. journal_entries ───────────────────────────────────────────────────────
-- id: BIGINT AUTO_INCREMENT, child_id: VARCHAR(36) giữ nguyên để tương thích Android
CREATE TABLE journal_entries (
    id           BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT PRIMARY KEY,
    child_id     VARCHAR(36)      NOT NULL,
    emotion_type VARCHAR(50)      NOT NULL,
    note         TEXT,
    created_at   TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_journal_child_id (child_id),
    INDEX idx_journal_created  (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── 5. practice_attempts ─────────────────────────────────────────────────────
-- id: BIGINT AUTO_INCREMENT, child_id: VARCHAR(36), scenario_id: INT UNSIGNED nullable
CREATE TABLE practice_attempts (
    id              BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT PRIMARY KEY,
    child_id        VARCHAR(36)      NOT NULL,
    scenario_id     INT UNSIGNED,
    is_correct      BOOLEAN          NOT NULL,
    prompt_emotion  VARCHAR(50),
    created_at      TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_practice_child_id (child_id),
    INDEX idx_practice_created  (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── 6. stories ───────────────────────────────────────────────────────────────
-- id: INT AUTO_INCREMENT, không seed — admin quản lý qua panel
CREATE TABLE stories (
    id          INT UNSIGNED  NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(200)  NOT NULL,
    content     TEXT          NOT NULL,
    category    VARCHAR(100)  NOT NULL DEFAULT 'general',
    image_url   VARCHAR(500)  NULL,
    sort_order  INT UNSIGNED  NOT NULL DEFAULT 0,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── 7. music_tracks ──────────────────────────────────────────────────────────
-- id: INT AUTO_INCREMENT, không seed — admin quản lý qua panel
CREATE TABLE music_tracks (
    id          INT UNSIGNED  NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(200)  NOT NULL,
    artist      VARCHAR(200)  NOT NULL DEFAULT '',
    filename    VARCHAR(300)  NOT NULL,
    sort_order  INT UNSIGNED  NOT NULL DEFAULT 0,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
