package com.emotionfriend.core.designsystem.components

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

    fun randomHome()      = home.random()
    fun randomLearn()     = learn.random()
    fun randomSituation() = situation.random()
    fun randomCamera()    = camera.random()
    fun randomRelax()     = relax.random()
    fun randomCorrect()   = correct.random()
    fun randomWrong()     = wrong.random()
    fun randomIdleHint()  = idleHint.random()
}
