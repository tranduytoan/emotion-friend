package com.emotionfriend.core.designsystem.components

import com.emotionfriend.domain.model.EmotionType

/**
 * All dialogue lines for the "Cô giáo My" companion character, organised by context.
 *
 * Rules:
 *  - Short (1–2 lines max)
 *  - Gentle, warm tone — no negative words ("sai rồi", "không đúng", "thua")
 *  - Wrong-answer messages use encouragement, never blame
 */
object TeacherMyMessages {

    val home = listOf(
        "Hôm nay mình cùng học cảm xúc nhé! 😊",
        "Chào con! Cô ở đây giúp con khám phá cảm xúc. 🌟",
        "Con chọn hoạt động nào mình thích nhé! 💛",
    )

    val learn = listOf(
        "Con hãy chọn cảm xúc giống khuôn mặt này nhé.",
        "Mình cùng nghe và nhận ra cảm xúc nhé con. 🔊",
        "Con nghe kỹ rồi chọn cảm xúc phù hợp nhé! 😊",
    )

    val situation = listOf(
        "Mình cùng xem bạn trong câu chuyện đang cảm thấy thế nào nhé.",
        "Con nghe câu chuyện rồi đoán cảm xúc của bạn nhé! 💭",
        "Mình cùng hiểu cảm xúc của bạn trong câu chuyện nhé con.",
    )

    val camera = listOf(
        "Con thử thể hiện khuôn mặt vui nhé! 😄",
        "Mình cùng luyện biểu cảm khuôn mặt nhé con. 📷",
        "Con làm khuôn mặt thật tự nhiên là được rồi. 🌸",
    )

    val relax = listOf(
        "Mình cùng thở chậm và thư giãn nhé. 🌸",
        "Con chọn hoạt động thư giãn mà con thích nhé! 🌈",
        "Hít thở nhẹ nhàng, mọi thứ đều ổn nhé con. 💛",
    )

    val correct = listOf(
        "Con làm tốt lắm! 🌟",
        "Tuyệt vời! Cô biết con làm được mà! ⭐",
        "Giỏi quá! Con hiểu cảm xúc rất tốt đó! 💛",
        "Xuất sắc! Con thật thông minh! 🌸",
    )

    val wrong = listOf(
        "Không sao đâu, mình thử lại nhẹ nhàng nhé. 🌈",
        "Gần rồi! Mình cùng thử thêm lần nữa nhé con. 💪",
        "Không sao cả! Học là phải thử mới tiến bộ nhé. 😊",
        "Cố lên con! Cô tin con sẽ làm được. 🌟",
    )

    val idleHint = listOf(
        "Con nhớ nhấn vào hình nhé! 👆",
        "Con thử chọn một hoạt động nào đó nhé! 😊",
    )

    fun journalSupport(emotion: EmotionType?): String = when (emotion) {
        EmotionType.HAPPY -> listOf(
            "Cô vui cùng con khi nghe chuyện vui đó! 🌟",
            "Niềm vui của con làm cô cũng thấy ấm áp nữa. 💛",
        ).random()
        EmotionType.SAD -> listOf(
            "Cô ở đây với con nhé, con không cần buồn một mình đâu. 💛",
            "Mình cùng ôm lấy cảm xúc buồn một chút rồi sẽ nhẹ hơn nhé. 🌸",
        ).random()
        EmotionType.ANGRY -> listOf(
            "Cô hiểu con đang khó chịu, mình hít thở chậm một chút nhé. 🌿",
            "Con cứ nói ra điều làm mình bực nhé, cô đang nghe con đây. 🤗",
        ).random()
        EmotionType.SURPRISED -> listOf(
            "Ôi, chuyện của con bất ngờ quá! Cô đang nghe nè. ✨",
            "Cảm xúc ngạc nhiên này thú vị quá, con kể cô nghe thêm nhé! 🌟",
        ).random()
        EmotionType.CALM -> listOf(
            "Con đang bình tĩnh rồi, mình cứ chia sẻ nhẹ nhàng nhé. 🌸",
            "Cảm ơn con đã kể cho cô nghe thật bình yên. 💛",
        ).random()
        EmotionType.TIRED -> listOf(
            "Con mệt rồi hả, mình nghỉ một chút cũng được nhé. ☁️",
            "Nếu con mệt, mình nói chậm thôi và thở nhẹ nhé. 🌙",
        ).random()
        null -> listOf(
            "Cô luôn sẵn sàng nghe con tâm sự nhé. 💛",
            "Con cứ kể chậm rãi, cô đang lắng nghe con đây. 🌸",
        ).random()
    }

    fun randomHome()      = home.random()
    fun randomLearn()     = learn.random()
    fun randomSituation() = situation.random()
    fun randomCamera()    = camera.random()
    fun randomRelax()     = relax.random()
    fun randomCorrect()   = correct.random()
    fun randomWrong()     = wrong.random()
    fun randomIdleHint()  = idleHint.random()
}
