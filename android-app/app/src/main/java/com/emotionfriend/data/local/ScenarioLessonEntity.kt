package com.emotionfriend.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.emotionfriend.domain.model.EmotionType

@Entity(tableName = "scenario_lessons")
data class ScenarioLessonEntity(
    @PrimaryKey val id: String,
    val title: String,
    val situationText: String,
    val imageName: String?,
    val correctEmotion: EmotionType,
    val options: List<EmotionType>,
    val explanation: String
)
