package com.emotionfriend.api.repository.fake

import com.emotionfriend.api.model.EmotionCard
import com.emotionfriend.api.model.EmotionType
import com.emotionfriend.api.repository.EmotionRepository

class FakeEmotionRepository : EmotionRepository {

    private val emotions = listOf(
        EmotionCard("emotion_happy", EmotionType.HAPPY, "😊", "Vui", "Cảm thấy hạnh phúc và thoải mái"),
        EmotionCard("emotion_sad", EmotionType.SAD, "😢", "Buồn", "Cảm thấy không vui hoặc thất vọng"),
        EmotionCard("emotion_angry", EmotionType.ANGRY, "😠", "Tức", "Cảm thấy bực bội hoặc không hài lòng"),
        EmotionCard("emotion_surprised", EmotionType.SURPRISED, "😮", "Ngạc nhiên", "Cảm thấy bất ngờ về điều gì đó"),
        EmotionCard("emotion_calm", EmotionType.CALM, "😌", "Bình thản", "Cảm thấy yên tĩnh và thoải mái"),
        EmotionCard("emotion_tired", EmotionType.TIRED, "😴", "Mệt", "Cảm thấy kiệt sức và muốn nghỉ ngơi"),
    )

    override suspend fun getAll(): List<EmotionCard> = emotions

    override suspend fun getById(id: String): EmotionCard? = emotions.find { it.id == id }
}
