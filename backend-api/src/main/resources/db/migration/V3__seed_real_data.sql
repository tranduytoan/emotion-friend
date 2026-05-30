-- V3: Seed dữ liệu thực - emotion cards và scenario lessons đầy đủ
-- Chạy sau V1 + V2 (Flyway tự động khi backend khởi động)

-- ─── Xóa seed mẫu từ V1/V2 và chèn dữ liệu chuẩn ───────────────────────────

-- emotion_cards: 6 cảm xúc cơ bản với mô tả đầy đủ cho trẻ em
INSERT IGNORE INTO emotion_cards (id, emotion_type, emoji, label, description, sort_order) VALUES
  ('emotion_happy',     'HAPPY',     '😄', 'Vui vẻ',    'Cảm giác vui vẻ, hạnh phúc khi có điều tốt xảy ra.',                1),
  ('emotion_sad',       'SAD',       '😢', 'Buồn',      'Cảm giác buồn bã khi mất đi thứ mình yêu thích hoặc nhớ ai đó.',    2),
  ('emotion_angry',     'ANGRY',     '😠', 'Tức giận',  'Cảm giác tức giận khi có điều gì đó không công bằng.',               3),
  ('emotion_surprised', 'SURPRISED', '😲', 'Ngạc nhiên','Cảm giác bất ngờ khi có điều gì đó xảy ra ngoài dự đoán.',          4),
  ('emotion_calm',      'CALM',      '😌', 'Bình tĩnh', 'Cảm giác yên lặng, thư thái, không lo lắng hay bồn chồn.',           5),
  ('emotion_tired',     'TIRED',     '😴', 'Mệt mỏi',   'Cảm giác mệt mỏi, muốn nghỉ ngơi sau khi làm việc hoặc chơi nhiều.',6);

-- scenario_lessons: 15 tình huống thực tế cho trẻ (thêm 5 mới ngoài V1+V2)
INSERT IGNORE INTO scenario_lessons (id, title, situation, options, correct_index, explanation, sort_order) VALUES
  ('scenario_11',
   'Chia sẻ đồ ăn',
   'Bạn đang ăn bánh ngon. Một bạn khác nhìn và hỏi "Bạn cho mình ăn cùng được không?". Con cảm thấy thế nào?',
   '["Tức giận","Vui khi chia sẻ","Buồn","Mệt mỏi"]',
   1,
   'Chia sẻ với bạn bè làm chúng ta cảm thấy vui vẻ và ấm áp hơn.',
   11),

  ('scenario_12',
   'Bị mắng khi chưa làm bài tập',
   'Con quên làm bài tập về nhà và bị cô giáo nhắc nhở trước cả lớp. Con cảm thấy thế nào?',
   '["Vui vẻ","Bình thản","Xấu hổ và buồn","Ngạc nhiên"]',
   2,
   'Khi bị nhắc nhở trước đám đông, chúng ta thường cảm thấy xấu hổ và buồn.',
   12),

  ('scenario_13',
   'Xem phim hoạt hình yêu thích',
   'Con được xem chương trình hoạt hình yêu thích vào cuối tuần. Con cảm thấy thế nào?',
   '["Buồn","Mệt mỏi","Tức giận","Vui vẻ và háo hức"]',
   3,
   'Làm việc mình yêu thích giúp chúng ta cảm thấy vui vẻ và háo hức.',
   13),

  ('scenario_14',
   'Mất điện đột ngột',
   'Con đang xem tivi thì điện mất đột ngột. Con cảm thấy thế nào?',
   '["Vui vẻ","Tức giận và thất vọng","Bình thản","Mệt mỏi"]',
   1,
   'Khi việc mình đang làm bị gián đoạn đột ngột, chúng ta thường cảm thấy tức giận và thất vọng.',
   14),

  ('scenario_15',
   'Được khen ngợi trước lớp',
   'Cô giáo khen con vẽ đẹp nhất lớp và trưng bày tranh của con lên bảng. Con cảm thấy thế nào?',
   '["Buồn","Tức giận","Vui vẻ và tự hào","Mệt mỏi"]',
   2,
   'Được khen ngợi và công nhận trước mọi người làm chúng ta cảm thấy vui vẻ và tự hào.',
   15);

-- ─── children: tạo profile mặc định cho trẻ ────────────────────────────────
INSERT IGNORE INTO children (id, name, age, avatar_emoji) VALUES
  ('default_child', 'Bé yêu', 6, '🧒');
