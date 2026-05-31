-- V8: Làm gọn schema theo hướng sản phẩm thực tế hơn.
-- - Xoá emotion_cards, practice_attempts, children
-- - Gộp story_images vào stories bằng cột image_folder
-- - Thêm bảng accounts để phục vụ đăng nhập/quản trị sau này

-- ── 1. Dọn các bảng rối / không còn cần thiết ──────────────────────────────
DROP TABLE IF EXISTS practice_attempts;
DROP TABLE IF EXISTS emotion_cards;
DROP TABLE IF EXISTS children;

-- ── 2. Gộp story_images vào stories ────────────────────────────────────────
ALTER TABLE stories
    ADD COLUMN image_folder VARCHAR(200) NULL AFTER image_url;

UPDATE stories s
LEFT JOIN story_images si ON si.story_id = s.id
SET s.image_folder = si.folder_name;

DROP TABLE IF EXISTS story_images;

-- ── 3. Bảng account ────────────────────────────────────────────────────────
CREATE TABLE accounts (
    id          INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    account     VARCHAR(255) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    name        VARCHAR(150) NOT NULL,
    age         TINYINT UNSIGNED NOT NULL,
    avatar_url  VARCHAR(500) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_accounts_account (account)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Demo account: demo@gmail.com / 123456
INSERT INTO accounts (account, password, name, age, avatar_url)
VALUES (
    'demo@gmail.com',
    SHA2('123456', 256),
    'Demo Vy',
    8,
    'avatar/demo.png'
);