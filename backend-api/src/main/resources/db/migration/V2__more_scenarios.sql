-- V2: Thêm 5 tình huống học cảm xúc mới
-- Phủ đủ 6 loại cảm xúc: HAPPY, SAD, ANGRY, SURPRISED, CALM, TIRED

INSERT IGNORE INTO scenario_lessons
    (id, title, situation, options, correct_index, explanation, sort_order)
VALUES
  ('scenario_6',
   'Không mở được đồ chơi',
   'Con rất muốn chơi đồ chơi mới nhưng mãi không mở được hộp. Con cảm thấy thế nào?',
   '["Vui vẻ","Tức giận và bực bội","Ngạc nhiên","Bình thản"]',
   1,
   'Khi không làm được điều mình muốn, chúng ta thường cảm thấy tức giận và bực bội.',
   6),

  ('scenario_7',
   'Phải dậy sớm đi học',
   'Hôm nay con phải dậy rất sớm để đi học trong khi vẫn còn buồn ngủ. Con cảm thấy thế nào?',
   '["Vui vẻ","Ngạc nhiên","Mệt mỏi và buồn ngủ","Tức giận"]',
   2,
   'Khi phải dậy sớm hơn bình thường, chúng ta thường cảm thấy mệt mỏi và buồn ngủ.',
   7),

  ('scenario_8',
   'Gặp lại bạn cũ',
   'Con gặp lại người bạn thân mà đã lâu không gặp. Con cảm thấy thế nào?',
   '["Buồn","Mệt mỏi","Tức giận","Vui và ngạc nhiên"]',
   3,
   'Gặp lại bạn thân sau thời gian xa cách khiến chúng ta cảm thấy vui và ngạc nhiên.',
   8),

  ('scenario_9',
   'Bị ướt vì mưa bất ngờ',
   'Con đang chơi ngoài sân thì trời mưa to bất ngờ và con bị ướt hết. Con cảm thấy thế nào?',
   '["Vui vẻ","Bình thản","Khó chịu và tức giận","Ngạc nhiên"]',
   2,
   'Khi bị ướt bất ngờ vì mưa, chúng ta thường cảm thấy khó chịu và tức giận.',
   9),

  ('scenario_10',
   'Nghe nhạc nhẹ trước khi ngủ',
   'Trước khi ngủ, con nghe nhạc nhẹ nhàng và nằm thư giãn. Con cảm thấy thế nào?',
   '["Tức giận","Bình thản và thư giãn","Buồn","Mệt và khó chịu"]',
   1,
   'Nghe nhạc nhẹ và thư giãn giúp chúng ta cảm thấy bình thản và dễ ngủ hơn.',
   10);
