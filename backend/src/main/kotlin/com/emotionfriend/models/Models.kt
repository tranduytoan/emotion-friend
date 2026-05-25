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

@Serializable
data class User(
    val id: Long,
    val name: String,
    val age: Int,
    val avatarUrl: String? = null
)

@Serializable
data class Settings(
    val userId: Long,
    val soundEnabled: Boolean,
    val notificationEnabled: Boolean,
    val reminderTime: String,
    val language: String
)

@Serializable
data class UserProfile(
    val user: User,
    val settings: Settings,
    val progress: Progress
)

// ── Request bodies ────────────────────────────────────────────────────────────

@Serializable
data class EmotionLogRequest(
    val userId: Long,
    val emotionId: Int,
    val note: String? = null
)

@Serializable
data class ProfileUpdateRequest(
    val userId: Long,
    val name: String? = null,
    val age: Int? = null,
    val avatarUrl: String? = null,
    val soundEnabled: Boolean? = null,
    val notificationEnabled: Boolean? = null,
    val reminderTime: String? = null,
    val language: String? = null
)
