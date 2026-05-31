package com.emotionfriend.api.model

import kotlinx.serialization.Serializable

@Serializable
data class LessonTopic(
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val difficulty: Int = 1,   // 1=dễ, 2=trung bình, 3=khó
    val sortOrder: Int = 0,
)

@Serializable
data class LessonTopicRequest(
    val title: String,
    val description: String = "",
    val difficulty: Int = 1,
    val sortOrder: Int = 0,
)
