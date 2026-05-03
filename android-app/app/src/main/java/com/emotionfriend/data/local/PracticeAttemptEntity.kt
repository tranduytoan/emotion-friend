package com.emotionfriend.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.emotionfriend.domain.model.EmotionType

@Entity(tableName = "practice_attempts")
data class PracticeAttemptEntity(
    @PrimaryKey val id: String,
    val childId: String,
    val taskType: String,
    val promptId: String,
    val selectedEmotion: EmotionType?,
    val correctEmotion: EmotionType?,
    val isCorrect: Boolean?,
    val createdAt: Long
)
