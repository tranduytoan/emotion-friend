-- V7: Thêm hệ thống chủ đề (lesson_topics) và ảnh truyện (story_images).
-- Mỗi chủ đề có 8 câu hỏi, sắp xếp từ dễ đến khó.
-- Bảng story_images lưu tên folder chứa 4 ảnh (1.jpg, 2.jpg, 3.jpg, 4.jpg) cho mỗi câu chuyện.

-- ── 1. Tạo bảng lesson_topics ─────────────────────────────────────────────────
CREATE TABLE lesson_topics (
    id          INT UNSIGNED  NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(200)  NOT NULL,
  description TEXT          NOT NULL,
    difficulty  TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '1=dễ, 2=trung bình, 3=khó',
    sort_order  SMALLINT UNSIGNED NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── 2. Thêm topic_id vào scenario_lessons ─────────────────────────────────────
ALTER TABLE scenario_lessons ADD COLUMN topic_id INT UNSIGNED NULL AFTER sort_order;
ALTER TABLE scenario_lessons ADD INDEX idx_scenario_topic (topic_id);

-- ── 3. Tạo bảng story_images ──────────────────────────────────────────────────
-- Mỗi câu chuyện có đúng 4 ảnh đặt trong folder riêng (1.jpg, 2.jpg, 3.jpg, 4.jpg).
-- URL ảnh: {BASE_URL}/static/stories/{folder_name}/1.jpg
CREATE TABLE story_images (
    story_id    INT UNSIGNED NOT NULL,
    folder_name VARCHAR(200) NOT NULL,
    PRIMARY KEY (story_id),
    CONSTRAINT fk_story_images_story FOREIGN KEY (story_id) REFERENCES stories(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── 4. Seed 3 chủ đề ──────────────────────────────────────────────────────────
INSERT INTO lesson_topics (title, description, difficulty, sort_order) VALUES
  ('Nhận biết cảm xúc cơ bản',
   'Học nhận biết 6 cảm xúc cơ bản (vui, buồn, tức giận, ngạc nhiên, bình tĩnh, mệt mỏi) qua các tình huống đơn giản hàng ngày.',
   1, 1),
  ('Cảm xúc trong trường học',
   'Khám phá các cảm xúc xuất hiện trong môi trường học tập, bạn bè và hoạt động ở trường.',
   2, 2),
  ('Cảm xúc trong gia đình',
   'Nhận biết và chia sẻ cảm xúc khi ở cùng người thân trong gia đình qua các tình huống hàng ngày.',
   3, 3);

-- ── 5. Gán 7 câu hỏi hiện có vào Chủ đề 1 ────────────────────────────────────
UPDATE scenario_lessons SET topic_id = 1;

-- ── 6. Thêm câu hỏi thứ 8 cho Chủ đề 1 ──────────────────────────────────────
INSERT INTO scenario_lessons (title, situation, options, correct_emotion, explanation, sort_order, topic_id) VALUES
  ('Được khen ngợi trước lớp',
   'Cô giáo đọc bài viết của bé trước cả lớp và nói: "Bài này của bạn viết rất hay! Cả lớp hãy cùng vỗ tay nào!"',
   '["HAPPY","SURPRISED","CALM","SAD"]',
   'HAPPY',
   'Khi được khen ngợi và được mọi người công nhận, chúng ta thường cảm thấy vui vẻ và tự hào.',
   8, 1);

-- ── 7. Seed 8 câu hỏi Chủ đề 2: Cảm xúc trong trường học ────────────────────
INSERT INTO scenario_lessons (title, situation, options, correct_emotion, explanation, sort_order, topic_id) VALUES
  ('Không được vào đội',
   'Các bạn đang chọn đội chơi cho trận bóng. Bé xung phong nhưng đội nào cũng đã đủ người, không ai chọn bé.',
   '["SAD","ANGRY","CALM","HAPPY"]',
   'SAD',
   'Khi muốn tham gia nhưng không được chấp nhận, chúng ta thường cảm thấy buồn. Hãy hỏi xem lần sau có thể tham gia không nhé.',
   1, 2),
  ('Đạt điểm cao trong bài kiểm tra',
   'Cô giáo phát bài kiểm tra lại. Bé lật bài ra và thấy điểm 10 to đỏ chói, cô còn ghi "Giỏi lắm!" bên cạnh.',
   '["HAPPY","CALM","SURPRISED","SAD"]',
   'HAPPY',
   'Khi nhận được kết quả tốt sau khi cố gắng học tập, chúng ta cảm thấy rất vui vẻ và tự hào về bản thân.',
   2, 2),
  ('Bị bạn trêu chọc',
   'Trong giờ ra chơi, một nhóm bạn cứ bắt chước giọng nói của bé và cười to. Bé đứng đó không biết làm gì.',
   '["ANGRY","SAD","CALM","HAPPY"]',
   'ANGRY',
   'Khi bị người khác chế giễu, chúng ta có thể cảm thấy tức giận. Lúc đó hãy bình tĩnh nói với cô giáo để được giúp đỡ.',
   3, 2),
  ('Có bài kiểm tra bất ngờ',
   'Vào đầu giờ học, cô giáo thông báo: "Hôm nay chúng ta sẽ có bài kiểm tra 15 phút!" Bé không chuẩn bị trước.',
   '["SURPRISED","ANGRY","CALM","HAPPY"]',
   'SURPRISED',
   'Khi nghe thông báo bất ngờ mà chưa chuẩn bị, chúng ta thường cảm thấy ngạc nhiên và hơi hồi hộp.',
   4, 2),
  ('Được ngồi đọc sách tự do',
   'Hôm nay cô giáo cho cả lớp tự chọn sách yêu thích trong góc thư viện và đọc trong 30 phút. Bé ngồi vào góc yêu thích, lấy cuốn sách tranh đẹp nhất.',
   '["CALM","HAPPY","TIRED","ANGRY"]',
   'CALM',
   'Khi được làm việc mình thích trong không gian yên tĩnh và thoải mái, chúng ta thường cảm thấy bình tĩnh và thư giãn.',
   5, 2),
  ('Học thêm quá nhiều',
   'Hôm nay bé học từ sáng đến chiều tối: toán, tiếng Anh, vẽ, và đàn piano. Về đến nhà, bé chỉ muốn nằm xuống và nhắm mắt.',
   '["TIRED","ANGRY","SAD","HAPPY"]',
   'TIRED',
   'Khi phải tập trung và hoạt động liên tục trong thời gian dài, cơ thể và tâm trí đều cảm thấy mệt mỏi. Đó là lúc cần nghỉ ngơi.',
   6, 2),
  ('Bạn thân nghỉ bệnh lâu ngày đã đến lớp',
   'Bạn thân nhất của bé đã nghỉ học hai tuần vì bệnh. Sáng nay bước vào lớp, bé thấy bạn đang ngồi ở chỗ cũ, mỉm cười vẫy tay.',
   '["SURPRISED","HAPPY","CALM","SAD"]',
   'SURPRISED',
   'Khi gặp lại người mình nhớ sau một thời gian dài không gặp mà không báo trước, chúng ta thường cảm thấy ngạc nhiên và rất vui.',
   7, 2),
  ('Cùng bạn hoàn thành dự án',
   'Bé và các bạn cùng làm poster về bảo vệ môi trường suốt cả buổi chiều. Cuối cùng poster rất đẹp, cô giáo chọn trưng bày trước lớp.',
   '["HAPPY","CALM","SURPRISED","TIRED"]',
   'HAPPY',
   'Khi cùng nhau nỗ lực và đạt được kết quả tốt, chúng ta cảm thấy vui vẻ và hài lòng về công sức mình đã bỏ ra.',
   8, 2);

-- ── 8. Seed 8 câu hỏi Chủ đề 3: Cảm xúc trong gia đình ──────────────────────
INSERT INTO scenario_lessons (title, situation, options, correct_emotion, explanation, sort_order, topic_id) VALUES
  ('Ba đi công tác xa',
   'Sáng nay ba xách vali đi sân bay. Ba nói sẽ đi công tác 2 tuần mới về. Bé đứng ở cửa nhìn theo xe của ba.',
   '["SAD","CALM","ANGRY","HAPPY"]',
   'SAD',
   'Khi phải xa người thân yêu một thời gian, chúng ta thường cảm thấy buồn. Cảm xúc này là bình thường và cho thấy chúng ta yêu thương họ.',
   1, 3),
  ('Tổ chức sinh nhật cho mẹ',
   'Cả nhà bí mật chuẩn bị bữa tiệc sinh nhật cho mẹ. Khi mẹ bước vào phòng, mọi người hét to "Sinh nhật vui vẻ!" và bánh kem được thắp nến sáng lên.',
   '["HAPPY","SURPRISED","CALM","SAD"]',
   'HAPPY',
   'Khi làm điều bất ngờ cho người mình yêu thương và thấy họ vui, chúng ta cảm thấy hạnh phúc và tự hào vì đã làm được điều tốt.',
   2, 3),
  ('Em bé ngã và khóc bất ngờ',
   'Bé đang ngồi đọc sách thì em bé ngồi cạnh bỗng ngã xuống sàn và khóc thét lên rất to và đột ngột.',
   '["SURPRISED","ANGRY","CALM","HAPPY"]',
   'SURPRISED',
   'Khi có điều gì đó xảy ra đột ngột và to lớn xung quanh mà mình không biết trước, chúng ta thường giật mình và cảm thấy ngạc nhiên.',
   3, 3),
  ('Em lấy đồ chơi của mình',
   'Bé đang xếp bộ lego yêu thích thì em vào phòng, nhặt mấy miếng lego đẹp nhất rồi bỏ đi không trả lại.',
   '["ANGRY","SAD","SURPRISED","CALM"]',
   'ANGRY',
   'Khi đồ vật của mình bị lấy mà không hỏi phép, chúng ta thường cảm thấy tức giận. Hãy nói chuyện nhẹ nhàng với em và giải thích tại sao cần xin phép.',
   4, 3),
  ('Đọc sách cùng ông bà',
   'Tối cuối tuần, cả nhà ngồi quây quần. Ông kể chuyện cổ tích, bà ngồi đan len bên cạnh. Bé ngả đầu vào vai ông nghe kể chuyện.',
   '["CALM","HAPPY","TIRED","SAD"]',
   'CALM',
   'Khi ở cạnh người thân yêu trong không khí ấm cúng và yên bình, chúng ta thường cảm thấy bình tĩnh, thư giãn và được yêu thương.',
   5, 3),
  ('Dọn dẹp nhà cả ngày',
   'Cuối tuần cả nhà tổng vệ sinh: quét nhà, lau kính, dọn tủ, giặt chăn. Bé phụ ba mẹ từ sáng đến chiều tối.',
   '["TIRED","HAPPY","CALM","ANGRY"]',
   'TIRED',
   'Sau khi làm việc nhà liên tục nhiều giờ liền, cơ thể mệt mỏi là điều rất tự nhiên. Đó là dấu hiệu bạn đã làm việc chăm chỉ và cần được nghỉ ngơi.',
   6, 3),
  ('Nhận tin bất ngờ',
   'Mẹ gọi cả nhà lại và nói: "Ba vừa được thăng chức! Cuối tuần này cả nhà mình sẽ đi ăn nhà hàng để ăn mừng nhé!"',
   '["SURPRISED","HAPPY","CALM","SAD"]',
   'SURPRISED',
   'Khi nghe tin tức vui và bất ngờ mà mình chưa biết trước, chúng ta thường cảm thấy ngạc nhiên, rồi sau đó là vui vẻ.',
   7, 3),
  ('Cả nhà đi du lịch',
   'Sáng sớm, cả nhà cùng nhau lên xe bắt đầu chuyến đi biển. Ba mở nhạc vui, mẹ mang theo hộp bánh ngon, bé và em ngồi nhìn ra cửa sổ xem cảnh đẹp.',
   '["HAPPY","CALM","SURPRISED","TIRED"]',
   'HAPPY',
   'Khi được đi chơi cùng những người thân yêu đến nơi mình thích, chúng ta thường cảm thấy rất vui vẻ và hạnh phúc.',
   8, 3);
