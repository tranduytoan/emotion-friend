-- V1: Initial schema for Emotion Friend API

CREATE TABLE IF NOT EXISTS journal_entries (
    id          VARCHAR(36)  NOT NULL PRIMARY KEY,
    child_id    VARCHAR(100) NOT NULL,
    emotion_type VARCHAR(50) NOT NULL,
    note        TEXT,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_journal_child_id (child_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS practice_attempts (
    id             VARCHAR(36)  NOT NULL PRIMARY KEY,
    child_id       VARCHAR(100) NOT NULL,
    scenario_id    VARCHAR(100) NOT NULL,
    selected_index INT          NOT NULL,
    is_correct     BOOLEAN      NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_practice_child_id (child_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
