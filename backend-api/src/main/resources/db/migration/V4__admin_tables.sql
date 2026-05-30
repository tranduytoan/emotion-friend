-- V4: Add stories and music_tracks tables for admin panel management

CREATE TABLE IF NOT EXISTS stories (
    id          VARCHAR(36)   NOT NULL PRIMARY KEY,
    title       VARCHAR(200)  NOT NULL,
    content     TEXT          NOT NULL,
    category    VARCHAR(100)  NOT NULL DEFAULT 'general',
    image_url   VARCHAR(500)  NULL,
    sort_order  INT UNSIGNED  NOT NULL DEFAULT 0,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS music_tracks (
    id          VARCHAR(36)   NOT NULL PRIMARY KEY,
    title       VARCHAR(200)  NOT NULL,
    artist      VARCHAR(200)  NOT NULL DEFAULT '',
    filename    VARCHAR(300)  NOT NULL,
    sort_order  INT UNSIGNED  NOT NULL DEFAULT 0,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seed default stories
INSERT INTO stories (id, title, content, category, sort_order) VALUES
('story-001', 'Cậu bé và cơn tức giận', 'Hôm nay ở trường, Nam bị bạn Minh lấy đồ chơi mà không hỏi. Nam cảm thấy rất tức giận và muốn la hét. Nhưng Nam đã hít thở sâu và nói: "Bạn Minh ơi, bạn có thể trả đồ chơi cho mình không?" Minh xin lỗi và trả lại. Nam cảm thấy vui hơn nhiều.', 'anger', 1),
('story-002', 'Ngày đầu tiên đến trường', 'Hôm nay là ngày đầu tiên Linh đến trường mới. Linh cảm thấy rất lo lắng và hồi hộp. Cô giáo giới thiệu Linh với cả lớp. Một bạn tên Hoa đến bắt tay và nói: "Chào bạn, mình là Hoa!". Linh mỉm cười và cảm thấy bớt lo lắng hơn.', 'anxiety', 2),
('story-003', 'Khi bị điểm kém', 'An nhận bài kiểm tra với điểm thấp. An cảm thấy buồn và xấu hổ. Mẹ thấy An buồn và nói: "Con có thể kể cho mẹ nghe không?" An kể hết mọi chuyện. Mẹ ôm An và nói: "Lần sau mình cùng ôn bài nhé." An cảm thấy được an ủi.', 'sadness', 3);

-- Seed default music tracks
INSERT INTO music_tracks (id, title, artist, filename, sort_order) VALUES
('music-001', 'Ánh Nắng Bình Yên', '', 'soft_music_1', 1),
('music-002', 'Giọt Mưa Thu', '', 'soft_music_2', 2),
('music-003', 'Tiếng Sóng Biển', '', 'soft_music_3', 3),
('music-004', 'Gió Nhẹ Đồng Quê', '', 'soft_music_4', 4),
('music-005', 'Suối Reo Sớm Mai', '', 'soft_music_5', 5),
('music-006', 'Bầu Trời Xanh', '', 'soft_music_6', 6);
