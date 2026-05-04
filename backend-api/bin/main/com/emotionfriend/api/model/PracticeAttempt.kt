package com.emotionfriend.api.model

import kotlinx.serialization.Serializable

@Serializable
data class PracticeAttempt(
    val id: String = "",
    val childId: String,
    val scenarioId: String,
    val selectedIndex: Int,
    val isCorrect: Boolean,
    val createdAt: String = "",
)
