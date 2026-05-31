package com.emotionfriend.api.model

import kotlinx.serialization.Serializable

@Serializable
data class ScenarioLesson(
    val id: Int = 0,
    val title: String,
    val situation: String,
    val options: List<String>,       // EmotionType codes: ["HAPPY","ANGRY","CALM","SURPRISED"]
    val correctEmotion: String,      // EmotionType code of the correct answer
    val explanation: String,
    val sortOrder: Int = 0,
    val topicId: Int? = null,
)

@Serializable
data class ScenarioLessonRequest(
    val title: String,
    val situation: String,
    val options: List<String>,
    val correctEmotion: String,
    val explanation: String,
    val sortOrder: Int = 0,
    val topicId: Int? = null,
)
