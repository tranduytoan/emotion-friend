package com.emotionfriend.models

import kotlinx.serialization.Serializable

// ── Domain models ─────────────────────────────────────────────────────────────

@Serializable
data class Emotion(
    val id: Int,
    val name: String,
    val displayName: String,
    val emoji: String,
    val colorHex: String,
    val description: String? = null
)

@Serializable
data class Situation(
    val id: Int,
    val title: String,
    val description: String? = null,
    val emotionId: Int,
    val imageUrl: String? = null,
    val difficultyLevel: String = "easy"
)

@Serializable
data class Progress(
    val userId: Long,
    val totalExercises: Int,
    val correctAnswers: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastActivityDate: String? = null
)

@Serializable
data class EmotionLog(
    val id: Long? = null,
    val userId: Long,
    val emotionId: Int,
    val note: String? = null,
    val loggedAt: String? = null
)

// ── Request bodies ────────────────────────────────────────────────────────────

@Serializable
data class EmotionLogRequest(
    val userId: Long,
    val emotionId: Int,
    val note: String? = null
)
