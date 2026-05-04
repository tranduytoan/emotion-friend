-- V1: Initial schema for Emotion Friend API
-- All tables use utf8mb4 for full Unicode (emoji) support.

-- ─────────────────────────────────────────────
-- 1. children
--    One row per child profile (MVP: single default child supported)
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS children (
    id           VARCHAR(36)   NOT NULL PRIMARY KEY,
    name         VARCHAR(100)  NOT NULL,
    age          TINYINT UNSIGNED,
    avatar_emoji VARCHAR(10)   DEFAULT '🧒',
    created_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────
-- 2. emotion_cards
--    Reference data: one row per emotion type
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS emotion_cards (
    id           VARCHAR(50)   NOT NULL PRIMARY KEY,
    emotion_type VARCHAR(50)   NOT NULL,
    emoji        VARCHAR(10)   NOT NULL,
    label        VARCHAR(100)  NOT NULL,
    description  TEXT          NOT NULL,
    sort_order   TINYINT UNSIGNED NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seed reference data
INSERT IGNORE INTO emotion_cards (id, emotion_type, emoji, label, description, sort_order) VALUES
  ('emotion_happy',     'HAPPY',     '😊', 'Vui',       'Cảm thấy hạnh phúc và thoải mái',          1),
  ('emotion_sad',       'SAD',       '😢', 'Buồn',      'Cảm thấy không vui hoặc thất vọng',         2),
  ('emotion_angry',     'ANGRY',     '😠', 'Tức',       'Cảm thấy bực bội hoặc không hài lòng',      3),
  ('emotion_surprised', 'SURPRISED', '😮', 'Ngạc nhiên','Cảm thấy bất ngờ về điều gì đó',            4),
  ('emotion_calm',      'CALM',      '😌', 'Bình thản', 'Cảm thấy yên tĩnh và thoải mái',            5),
  ('emotion_tired',     'TIRED',     '😴', 'Mệt',       'Cảm thấy kiệt sức và muốn nghỉ ngơi',       6);

-- ─────────────────────────────────────────────
-- 3. scenario_lessons
--    Situation-based learning scenarios; options stored as JSON array
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS scenario_lessons (
    id            VARCHAR(50)   NOT NULL PRIMARY KEY,
    title         VARCHAR(200)  NOT NULL,
    situation     TEXT          NOT NULL,
    -- JSON array of strings, e.g. ["Vui vẻ", "Buồn", ...]
    options       JSON          NOT NULL,
    correct_index TINYINT UNSIGNED NOT NULL,
    explanation   TEXT          NOT NULL,
    sort_order    SMALLINT UNSIGNED NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seed scenario reference data
INSERT IGNORE INTO scenario_lessons
    (id, title, situation, options, correct_index, explanation, sort_order)
VALUES
  ('scenario_1',
   'Bạn bị vấp ngã',
   'Bạn đang chạy trong sân trường thì bị vấp ngã. Bạn cảm thấy thế nào?',
   '["Vui vẻ","Đau và buồn","Ngạc nhiên vui","Tức giận"]',
   1,
   'Khi bị ngã, chúng ta thường cảm thấy đau và buồn.',
   1),
  ('scenario_2',
   'Nhận quà bất ngờ',
   'Ba mẹ tặng bạn một món quà bất ngờ vào sinh nhật. Bạn cảm thấy thế nào?',
   '["Buồn","Tức giận","Vui và ngạc nhiên","Mệt mỏi"]',
   2,
   'Nhận quà bất ngờ thường khiến chúng ta cảm thấy vui và ngạc nhiên.',
   2),
  ('scenario_3',
   'Bị mất đồ chơi yêu thích',
   'Bạn không tìm thấy món đồ chơi yêu thích của mình. Bạn cảm thấy thế nào?',
   '["Vui","Bình thản","Buồn và lo lắng","Ngạc nhiên"]',
   2,
   'Mất đồ vật yêu thích khiến chúng ta cảm thấy buồn và lo lắng.',
   3),
  ('scenario_4',
   'Được điểm cao',
   'Bạn vừa nhận kết quả bài kiểm tra và được điểm rất cao. Bạn cảm thấy thế nào?',
   '["Mệt mỏi","Vui và tự hào","Buồn","Tức giận"]',
   1,
   'Đạt thành tích tốt khiến chúng ta cảm thấy vui và tự hào.',
   4),
  ('scenario_5',
   'Bạn bè không cho chơi cùng',
   'Các bạn trong lớp đang chơi nhưng không cho bạn tham gia. Bạn cảm thấy thế nào?',
   '["Vui","Ngạc nhiên vui","Buồn và tủi thân","Bình thản"]',
   2,
   'Bị loại ra khỏi nhóm khiến chúng ta cảm thấy buồn và tủi thân.',
   5);

-- ─────────────────────────────────────────────
-- 4. journal_entries
--    Emotional diary written by / for a child
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS journal_entries (
    id           VARCHAR(36)  NOT NULL PRIMARY KEY,
    child_id     VARCHAR(36)  NOT NULL,
    emotion_type VARCHAR(50)  NOT NULL,
    note         TEXT,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_journal_child_id  (child_id),
    INDEX idx_journal_created   (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────
-- 5. practice_attempts
--    Records each scenario quiz answer.
--    Camera practice: stores only the prompted emotion + result — no images.
-- ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS practice_attempts (
    id              VARCHAR(36)  NOT NULL PRIMARY KEY,
    child_id        VARCHAR(36)  NOT NULL,
    -- scenario_id references scenario_lessons.id (soft FK for MVP)
    scenario_id     VARCHAR(50)  NOT NULL,
    selected_index  TINYINT UNSIGNED NOT NULL,
    is_correct      BOOLEAN      NOT NULL,
    -- For camera-practice rows: the emotion the child was asked to express
    prompt_emotion  VARCHAR(50)  NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_practice_child_id (child_id),
    INDEX idx_practice_created  (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
