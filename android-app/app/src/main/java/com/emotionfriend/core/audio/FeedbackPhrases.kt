package com.emotionfriend.core.audio

/**
 * Pool of diverse TTS feedback phrases for correct / incorrect answers.
 * Call [randomCorrect] or [randomIncorrect] each time an answer is submitted
 * so children hear different encouragement rather than the same line every time.
 */
object FeedbackPhrases {

    private val correct = listOf(
        "Chính xác! Con làm tốt lắm.",
        "Tuyệt vời! Con rất thông minh.",
        "Giỏi quá! Con hiểu rất rõ cảm xúc này.",
        "Xuất sắc! Ba mẹ sẽ rất tự hào về con.",
        "Đúng rồi! Con đang học rất giỏi.",
        "Wao! Con làm được rồi!",
        "Siêu lắm! Con rất hiểu cảm xúc.",
        "Chính xác rồi! Con thật thông minh.",
        "Giỏi lắm! Tiếp tục phát huy nhé con!",
    )

    private val incorrect = listOf(
        "Chưa đúng. Không sao, con thử lại lần sau nhé.",
        "Chưa chính xác. Mình cùng học thêm nhé.",
        "Không sao. Mỗi lần thử là mỗi lần tiến bộ.",
        "Tiếp tục cố gắng nhé! Con sẽ làm được.",
        "Gần đúng rồi! Con hãy thử thêm lần nữa nhé.",
        "Không sao cả! Học là phải thử và sai mới tiến bộ.",
    )

    fun randomCorrect(): String = correct.random()
    fun randomIncorrect(): String = incorrect.random()
}
