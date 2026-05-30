package com.emotionfriend.api.model

import kotlinx.serialization.Serializable

@Serializable
data class PracticeAttempt(
    val id: Long = 0L,
    val childId: String,
    val scenarioId: Int? = null,
    val isCorrect: Boolean,
    val promptEmotion: String? = null,
    val createdAt: String = "",
)
