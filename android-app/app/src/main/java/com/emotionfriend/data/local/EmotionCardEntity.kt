package com.emotionfriend.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.emotionfriend.domain.model.EmotionType

@Entity(tableName = "emotion_cards")
data class EmotionCardEntity(
    @PrimaryKey val id: String,
    val label: String,
    val emoji: String,
    val type: EmotionType,
    val description: String
)
