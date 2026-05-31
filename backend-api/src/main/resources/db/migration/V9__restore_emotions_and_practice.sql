-- V9: Restore DB-backed runtime tables required by API endpoints.
-- Goal: eliminate hardcoded/in-memory repositories for emotions and practice attempts.

CREATE TABLE IF NOT EXISTS emotion_cards (
    id           INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    emotion_type VARCHAR(50)  NOT NULL,
    emoji        VARCHAR(10)  NOT NULL,
    label        VARCHAR(100) NOT NULL,
    description  TEXT         NOT NULL,
    sort_order   SMALLINT UNSIGNED NOT NULL DEFAULT 0,
    UNIQUE KEY uq_emotion_cards_type (emotion_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO emotion_cards (emotion_type, emoji, label, description, sort_order) VALUES
  ('HAPPY',     '😄', 'Vui vẻ',    'Cảm giác vui vẻ, hạnh phúc khi có điều tốt xảy ra.',                 1),
  ('SAD',       '😢', 'Buồn',      'Cảm giác buồn bã khi mất đi thứ mình yêu thích hoặc nhớ ai đó.',     2),
  ('ANGRY',     '😠', 'Tức giận',  'Cảm giác tức giận khi có điều gì đó không công bằng.',                3),
  ('SURPRISED', '😲', 'Ngạc nhiên','Cảm giác bất ngờ khi có điều gì đó xảy ra ngoài dự đoán.',           4),
  ('CALM',      '😌', 'Bình tĩnh', 'Cảm giác yên lặng, thư thái, không lo lắng hay bồn chồn.',            5),
  ('TIRED',     '😴', 'Mệt mỏi',   'Cảm giác mệt mỏi, muốn nghỉ ngơi sau khi làm việc hoặc chơi nhiều.', 6);

CREATE TABLE IF NOT EXISTS practice_attempts (
    id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    child_id       VARCHAR(36)     NOT NULL,
    scenario_id    INT UNSIGNED    NULL,
    is_correct     BOOLEAN         NOT NULL,
    prompt_emotion VARCHAR(50)     NULL,
    created_at     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_practice_child_id (child_id),
    INDEX idx_practice_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
