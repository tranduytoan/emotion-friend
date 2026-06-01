# 🎤 SCRIPT TRÌNH BÀY DỰ ÁN — EMOTION FRIEND
### Dành cho buổi demo trước giảng viên hướng dẫn

> **Người trình bày:** Nguyễn Trung Nghĩa  
> **Dự án:** Emotion Friend — Ứng dụng hỗ trợ trẻ tự kỷ rèn luyện kỹ năng cảm xúc  
> **Nền tảng:** Android (Kotlin / Jetpack Compose) + Ktor Backend + React Admin  
> **Giai đoạn:** P6 — Nguyên mẫu phần mềm có thể chạy được

---

## MỞ ĐẦU — Bối cảnh và lý do chọn đề tài

> *[Nhìn thẳng vào cô, nói chậm rãi và tự tin]*

"Thưa cô, theo thống kê của Tổ chức Y tế Thế giới (WHO), cứ 100 trẻ em thì có khoảng 1–2 trẻ mắc rối loạn phổ tự kỷ (ASD). Tại Việt Nam, con số này ước tính lên đến hàng trăm nghìn trẻ.

Trẻ tự kỷ gặp rất nhiều khó khăn trong việc **nhận diện, hiểu và biểu đạt cảm xúc** — đây là một trong những rào cản lớn nhất khiến các em gặp khó khăn khi giao tiếp và hoà nhập xã hội. Tuy nhiên, hầu hết các công cụ hỗ trợ hiện tại hoặc quá chuyên sâu về mặt trị liệu lâm sàng, hoặc không được thiết kế dành riêng cho đặc điểm nhận thức đặc thù của các em.

Nhóm chúng em nhận thấy cơ hội ở đây và xây dựng **Emotion Friend** — một ứng dụng Android giáo dục, thiết kế chuyên biệt cho trẻ ASD từ 4–10 tuổi, giúp các em học cảm xúc theo cách vui vẻ, nhẹ nhàng và có hệ thống."

---

## PHẦN 1 — Giới thiệu Kiến trúc hệ thống (1 phút)

> *[Có thể chỉ vào sơ đồ hoặc README nếu chiếu màn hình]*

"Hệ thống Emotion Friend gồm **3 lớp chính**:

- **Ứng dụng Android** — đây là sản phẩm chính, trẻ em sẽ dùng hằng ngày. Viết bằng Kotlin và Jetpack Compose — công nghệ hiện đại nhất của Android.
- **Backend API** — xây dựng bằng Ktor (Kotlin), chạy trên Docker, quản lý toàn bộ nội dung học: bài học, câu chuyện, câu hỏi tình huống, nhạc thư giãn.
- **Admin Web** — giao diện React dành cho người vận hành, để quản lý và cập nhật nội dung mà không cần đụng vào code.

Dữ liệu lưu trữ trên MySQL 8. Toàn bộ backend deploy bằng Docker Compose, có thể chạy trên VPS chỉ với 1 lệnh."

---

## PHẦN 2 — Nhân vật Cô Vy (1 phút)

> *[Mở màn hình app lên, để bé nhìn thấy cô Vy xuất hiện]*

"Trước khi đi vào từng tính năng, em muốn giới thiệu nhân vật trung tâm của ứng dụng: **Cô Vy**.

Cô Vy là một giáo viên ảo người Việt, được thiết kế với ngoại hình thân thiện, ấm áp, mặc trang phục giản dị màu pastel — đúng chuẩn 'cô giáo mầm non' gần gũi với trẻ em.

**Tại sao lại cần một nhân vật ảo?**

Trẻ tự kỷ thường **rất nhạy cảm với sự thay đổi và gương mặt người thật**. Việc giao tiếp với một nhân vật hoạt hình nhất quán giúp trẻ cảm thấy an toàn hơn, không bị áp lực xã hội như khi nói chuyện với người lạ thật sự.

Cô Vy có **6 biểu cảm khác nhau** — vui, buồn, tức giận, ngạc nhiên, bình thản, mệt mỏi — thay đổi theo ngữ cảnh của từng màn hình. Điều này giúp trẻ học cảm xúc không chỉ qua lời nói mà còn qua **hình ảnh khuôn mặt trực quan**.

Quan trọng nhất: **giọng nói của cô Vy đồng hành xuyên suốt toàn bộ ứng dụng**. Với trẻ có khả năng đọc hạn chế, tiếng nói hướng dẫn là yếu tố thiết yếu để trẻ có thể tự sử dụng app mà không cần bố mẹ kèm theo từng bước."

---

## PHẦN 3 — Demo lần lượt từng màn hình

---

### 🌅 3.1 — Màn hình Check-in Cảm xúc hằng ngày (`DailyCheckInScreen`)

> *[Mở app lần đầu trong ngày — màn hình check-in xuất hiện]*

"Khi mở app lên, điều đầu tiên trẻ thấy là **màn hình hỏi thăm của cô Vy**: 'Hôm nay con đang cảm thấy thế nào?'

Trẻ chọn một trong các icon cảm xúc — vui 😊, buồn 😢, tức giận 😠, ngạc nhiên 😮, bình thản 😌, mệt mỏi 😴 — và có thể ghi một câu ngắn nếu muốn. Ngoài ra còn có **chức năng thu âm giọng nói**, vì với trẻ nhỏ, nói đôi khi dễ hơn viết.

**Tại sao thiết kế như vậy?**

Với trẻ tự kỷ, cảm xúc thường bị dồn nén bởi các em không biết cách diễn đạt. Việc có một 'nghi lễ' check-in mỗi ngày vừa giúp trẻ **hình thành thói quen nhận thức cảm xúc**, vừa tạo ra **khoảng không gian an toàn** để các em chia sẻ mà không sợ bị phán xét.

Bố mẹ cũng có thể xem lại nhật ký này trong trang 'Cảm xúc của con' — đây là cầu nối quan trọng giữa trẻ và phụ huynh, đặc biệt với những điều trẻ không dám nói thẳng."

---

### 🏠 3.2 — Trang Dashboard (Home Screen — `HomeScreen`)

> *[Sau check-in, màn hình chính hiện ra]*

"Đây là màn hình chính — **dashboard** của ứng dụng.

Thiết kế cực kỳ tối giản: **chỉ 6 card hoạt động** sắp xếp gọn gàng, mỗi card có một emoji lớn và nhãn chữ ngắn. Phía trên màn hình là cô Vy đang 'chào' và hướng dẫn bằng giọng nói.

**Tại sao không đặt thêm thông tin vào dashboard?**

Nghiên cứu về nhận thức cho trẻ ASD cho thấy các em rất dễ bị **quá tải thông tin (sensory overload)** khi màn hình có quá nhiều text, màu sắc, hay thành phần di chuyển. Chúng em cố ý giữ tối đa **1 thông điệp tại một thời điểm**, để trẻ chỉ cần chú ý đến giọng cô Vy và một card duy nhất mình muốn chọn.

Palette màu sắc của toàn app cũng được thiết kế theo chuẩn **'Autism-friendly UI'**: xanh trời nhạt (Sky Blue), xanh bạc hà (Mint Green), nền kem ấm (Warm Cream) — tất cả đều dịu, không chói, không gây kích thích quá mức."

---

### 📚 3.3 — Học Cảm Xúc (`LearnScreen`)

> *[Nhấn vào card 'Học cảm xúc']*

"Đây là module **học cảm xúc cốt lõi** của ứng dụng.

Cô Vy sẽ hỏi: 'Nhìn vào bức tranh này, con nghĩ bạn nhỏ đang cảm thấy gì?' — kèm theo ảnh minh hoạ một tình huống hoặc khuôn mặt. Trẻ chọn đáp án từ **3–4 icon cảm xúc**; khi chạm vào mỗi icon, app sẽ **đọc tên cảm xúc đó bằng giọng cô Vy**.

Tại sao điều này quan trọng? Vì nhiều trẻ tự kỷ biết cảm xúc tồn tại nhưng **không gắn được tên gọi với biểu cảm khuôn mặt**. Việc vừa nhìn icon vừa nghe tên cảm xúc tạo ra một liên kết đa giác quan, hiệu quả hơn nhiều so với chỉ đọc hoặc chỉ nhìn.

Khi trả lời **đúng**: hiệu ứng chúc mừng sáng lên, cô Vy tươi cười và khen ngợi bằng giọng nói — 'Giỏi quá! Con thật thông minh!'

Khi trả lời **sai**: không có âm thanh to hay màu đỏ chói mắt gây sợ hãi. Thay vào đó là phản hồi nhẹ nhàng: 'Chưa đúng rồi, thử lại nào con!' Cô Vy vẫn hiện biểu cảm động viên.

**Tại sao phải xử lý phản hồi sai cẩn thận như vậy?** Trẻ tự kỷ rất hay có phản ứng cảm xúc mạnh (meltdown) khi gặp thất bại. Thiết kế phản hồi nhẹ nhàng giúp trẻ học được rằng 'sai không phải điều đáng sợ' — đây là một phần trong giáo dục cảm xúc chính trẻ cần học."

---

### 📖 3.4 — Kể Chuyện (`StoryScreen`)

> *[Quay lại Home → nhấn 'Kể chuyện']*

"Module **Kể chuyện** mang lại một cách học cảm xúc hoàn toàn khác — thông qua **truyện tranh có tường thuật âm thanh**.

Hiện tại app có **5 câu chuyện gốc**, mỗi câu được xây dựng công phu: có nhân vật, có tình huống có thật trong cuộc sống trẻ em, có đầu có cuối, có thông điệp cảm xúc rõ ràng. Ảnh minh hoạ được thiết kế riêng, mỗi trang chuyện là một tấm ảnh.

Cô Vy sẽ **kể chuyện** cho trẻ nghe bằng giọng nói nhẹ nhàng, kèm ảnh minh hoạ, trẻ có thể lật trang như đọc truyện tranh. Đến cuối câu chuyện, app hỏi: 'Con cảm thấy thế nào sau khi nghe câu chuyện này?' và để trẻ bày tỏ cảm xúc.

**Tại sao cần cả module kể chuyện?**

Cảm xúc không chỉ xuất hiện trong những câu hỏi quiz — nó xuất hiện trong **bối cảnh cuộc sống thực**. Kể chuyện giúp trẻ hiểu rằng buồn, vui, tức... đều là những phần tự nhiên của cuộc sống, và quan trọng là học cách ứng xử với từng cảm xúc đó. Phương pháp này còn giúp **phát triển trí nhớ narrative và ngôn ngữ xã hội** — hai điểm yếu thường gặp ở trẻ ASD."

---

### 💬 3.5 — Tâm Sự cùng Cô Vy (`ConfideScreen`)

> *[Quay lại Home → nhấn 'Tâm sự']*

"Đây có lẽ là tính năng **độc đáo và cảm xúc nhất** của ứng dụng.

Trang này là một giao diện **chat với AI** — nhưng phía bên kia không phải một chatbot lạnh lùng mà vẫn là **cô Vy**. Trẻ có thể nhập text hoặc **nói thẳng vào mic** (ứng dụng dùng Speech-to-Text), và cô Vy sẽ lắng nghe, đồng cảm, đưa ra lời khuyên phù hợp lứa tuổi.

Ví dụ trẻ nói: 'Con buồn vì bạn không chơi cùng con.' Cô Vy sẽ phản hồi: 'Ồ, nghe buồn quá nhỉ... Cô hiểu con. Khi buồn, mình có thể thử thở sâu, hoặc kể cho bố mẹ nghe. Con có muốn nghe cô kể một câu chuyện vui không?'

Backend kết nối **OpenAI API** để tạo phản hồi tự nhiên, được cá nhân hoá. Chúng em cũng xây thêm **logic dự phòng** — khi hết token API, app vẫn phản hồi được một số câu cơ bản dựa trên keyword nhận diện, đảm bảo trải nghiệm không bị đứt đoạn đột ngột.

**Tại sao tính năng này lại quan trọng?**

Nhiều trẻ tự kỷ cảm thấy khó nói chuyện với người thật vì lo sợ bị phán xét hay không hiểu. Một nhân vật ảo kiên nhẫn, không bao giờ mất kiên nhẫn, luôn sẵn sàng lắng nghe — đó là điều mà rất nhiều trẻ ASD thực sự cần."

---

### 📔 3.6 — Cảm Xúc Của Con (`JournalScreen`)

> *[Quay lại Home → nhấn 'Cảm xúc của con']*

"Trang này là **nhật ký cảm xúc** của bé, được lưu lại theo thời gian.

Mỗi entry gồm: ngày giờ, icon cảm xúc đã chọn, và nếu bé có ghi âm thì có thể **phát lại audio** ngay trong app. Bố mẹ hoặc chuyên viên trị liệu có thể cùng ngồi với bé, nghe lại những gì bé đã chia sẻ, từ đó hiểu hơn về thế giới cảm xúc của con.

Bé cũng có thể thêm một entry mới bất cứ lúc nào bằng nút FAB (+) — không nhất thiết chỉ check-in buổi sáng.

**Giá trị thực sự của tính năng này là gì?**

Với trẻ ASD, cảm xúc đến rất nhanh và qua đi cũng nhanh — nhưng không được xử lý đúng cách sẽ tích tụ thành căng thẳng. Việc lưu lại nhật ký giúp bố mẹ nhận biết **các pattern cảm xúc** của con theo tuần, theo tháng — ví dụ, con hay buồn vào ngày đầu tuần, hay tức giận sau giờ học — từ đó đưa ra hỗ trợ phù hợp."

---

### 🌈 3.7 — Thư Giãn (`RelaxScreen`)

> *[Quay lại Home → nhấn 'Thư giãn']*

"Khi bé đang căng thẳng, tức giận hay mệt mỏi, **trang Thư giãn** cung cấp các hoạt động giúp bé xả stress nhẹ nhàng.

Hiện tại có **2 hoạt động đã hoàn thiện**:

**① Thở cùng Bóng** — Một quả bóng co giãn nhịp nhàng, hít vào 3 giây — bóng to ra, thở ra 3 giây — bóng nhỏ lại. Cô Vy đếm nhịp thở cùng bé bằng giọng nói. Đây là kỹ thuật **thở 4-7-8** đơn giản hoá, được khuyến nghị trong trị liệu cảm giác cho trẻ ASD.

**② Nghe nhạc nhẹ thư giãn** — Danh sách nhạc không lời, âm thanh thiên nhiên, được tuyển chọn đặc biệt phù hợp cho trẻ tự kỷ — không có âm trầm mạnh, không có đột biến âm lượng.

Hoạt động **Xếp hình vui** hiện đang trong giai đoạn phát triển, app đã thông báo rõ ràng điều này thay vì để nguyên màn hình trống.

**Tại sao module thư giãn lại quan trọng trong một app dạy cảm xúc?**

Vì học cảm xúc không chỉ là nhận biết — mà còn là **điều tiết**. Nếu một đứa trẻ biết mình đang tức giận nhưng không có công cụ để bình tĩnh lại, kiến thức đó gần như vô dụng. Module thư giãn chính là bước 'hạ nhiệt' trong vòng lặp học — nhận biết — điều tiết."

---

### 📊 3.8 — Tiến Trình (`ProgressScreen`)

> *[Quay lại Home → nhấn 'Tiến trình']*

"Trang Tiến trình hiển thị **tổng hợp quá trình học** của bé: số bài đã hoàn thành, độ chính xác theo từng loại cảm xúc, ngày streak liên tiếp...

Bố mẹ và giáo viên có thể dùng trang này để theo dõi con đang tiến bộ ở điểm nào, còn đang gặp khó khăn với cảm xúc nào. Ví dụ nếu bé nhận diện đúng 'vui' 90% nhưng chỉ đúng 40% với 'ngạc nhiên' — đó là chỉ số để có thể tập trung hỗ trợ thêm.

Đây cũng là động lực cho chính bé — trẻ em rất thích nhìn thấy 'mình đã làm được bao nhiêu rồi'."

---

### 👤 3.9 — Hồ Sơ & Nhắc nhở (`ProfileScreen`)

> *[Quay lại Home → nhấn 'Hồ sơ']*

"Trang hồ sơ là trang cơ bản nhất — cập nhật tên, ảnh đại diện, thông tin của bé.

Tính năng đặc biệt là **thông báo nhắc giờ học**: phụ huynh có thể bật/tắt và chọn giờ nhắc hằng ngày, ví dụ 8 giờ tối. Đúng giờ đó, điện thoại sẽ báo 'Đến giờ học cảm xúc cùng cô Vy rồi!'

**Tại sao điều này lại có giá trị với trẻ ASD?**

Trẻ tự kỷ rất cần **routine — thói quen có lịch trình cố định**. Một giờ học cảm xúc cố định mỗi ngày, được nhắc nhở tự động, giúp trẻ biết trước và chuẩn bị tâm lý, giảm lo lắng và tăng tỉ lệ tham gia đều đặn."

---

### 🖥️ 3.10 — Admin Web Panel

> *[Mở trình duyệt, vào http://localhost:3000]*

"Cuối cùng là giao diện **Admin Web** — không phải dành cho trẻ, mà dành cho người vận hành nội dung.

Giáo viên hoặc chuyên viên trị liệu có thể đăng nhập và quản lý toàn bộ nội dung: thêm câu chuyện mới, thêm tình huống học, cập nhật bài nhạc thư giãn... mà không cần biết lập trình. Điều này đảm bảo ứng dụng có thể **phát triển nội dung liên tục** theo thời gian và nhu cầu thực tế từ các chuyên viên."

---

## KẾT LUẬN — Tổng kết giá trị của hệ thống

> *[Để điện thoại xuống, nhìn thẳng cô, nói từ trái tim]*

"Thưa cô, nhóm em không xây dựng Emotion Friend như một đồ án kỹ thuật thuần túy.

Chúng em xây dựng nó như một **người bạn đồng hành** — một công cụ mà một đứa trẻ 5 tuổi mắc chứng tự kỷ có thể tự mở ra, tự khám phá, và cảm thấy được hiểu.

Từng quyết định thiết kế đều có lý do: màu sắc dịu để không gây kích thích, icon lớn vì trẻ chưa đọc được nhiều, giọng nói xuyên suốt vì nghe dễ hơn đọc, phản hồi sai không đáng sợ vì trẻ cần học rằng thất bại ổn, AI để trẻ được lắng nghe bất cứ lúc nào.

Về mặt kỹ thuật, hệ thống đạt được:
- **Kiến trúc phân lớp rõ ràng** (Domain / Data / Feature) theo chuẩn Clean Architecture
- **Offline-first** — dữ liệu người dùng lưu local, không cần internet liên tục
- **Có thể mở rộng** — backend RESTful, nội dung quản lý qua Admin Web, thêm tính năng mới không ảnh hưởng tính năng cũ
- **Bảo mật hợp lý** — token xác thực, không lưu mật khẩu dạng plain text, API key không hardcode trong app
- **Triển khai đơn giản** — Docker Compose, 1 lệnh `docker compose up` là toàn bộ hệ thống backend sẵn sàng

Điều mà nhóm em tự hào nhất không phải là những tính năng đã làm được — mà là việc **đã đặt trẻ em vào trung tâm của mọi quyết định thiết kế và kỹ thuật**. Chúng em hi vọng đây là một minh chứng rằng công nghệ, khi được xây dựng với sự thấu hiểu và chú tâm, có thể thực sự tạo ra sự khác biệt trong cuộc sống của những đứa trẻ dễ tổn thương nhất.

Cảm ơn cô đã dành thời gian theo dõi phần trình bày của em. Em xin phép nhận câu hỏi từ cô ạ."

---

## PHỤ LỤC — Bảng câu hỏi có thể bị hỏi

| Câu hỏi | Gợi ý trả lời |
|---|---|
| **Tại sao chọn Kotlin / Jetpack Compose?** | Công nghệ hiện đại nhất của Android (2024+), UI khai báo dễ quản lý state, Google khuyến nghị cho app mới, giảm code boilerplate so với XML. |
| **Security: API Key OpenAI lưu ở đâu?** | Lưu trong file `.env` không commit vào Git, truyền qua biến môi trường Docker, app Android chỉ nhận qua backend — không bao giờ để key trong APK. |
| **Nếu mở rộng, phải làm gì?** | Thêm nội dung qua Admin Web, thêm ngôn ngữ (English), thêm module mới (Camera Practice), tích hợp phân tích cảm xúc qua camera ML Kit. |
| **Độ chính xác AI có đảm bảo không?** | Chúng em dùng prompt engineering cẩn thận, giới hạn ngữ cảnh cho trẻ em. Ngoài ra có fallback rule-based khi hết quota. Cần kiểm duyệt thêm từ chuyên gia tâm lý trong giai đoạn tiếp theo. |
| **Phụ huynh có thể xem gì?** | Nhật ký cảm xúc (Journal), audio bé đã ghi, biểu đồ tiến trình. Tính năng 'Parent Dashboard' riêng biệt là roadmap tương lai. |
| **Khác gì so với app khác trên thị trường?** | Thiết kế chuyên biệt cho trẻ Việt Nam + tự kỷ, có nhân vật ảo nhất quán, tích hợp AI đồng cảm, backend quản lý nội dung linh hoạt, mã nguồn mở. |

---

*Script được soạn dựa trên toàn bộ codebase, tài liệu kỹ thuật và thiết kế UI/UX của dự án Emotion Friend.*  
*Phiên bản: 1.0 — Ngày soạn: 01/06/2026*
