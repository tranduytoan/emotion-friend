package com.emotionfriend.domain.model

data class ScenarioLesson(
    val id: String,
    val title: String,
    val situationText: String,
    val imageName: String?,
    val correctEmotion: EmotionType,
    val options: List<EmotionType>,
    val explanation: String
)
