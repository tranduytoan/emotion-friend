package com.emotionfriend.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Mirrors backend ApiResponse<T> envelope. */
@Serializable
data class ApiResponseDto<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null,
)

// ── Response DTOs ─────────────────────────────────────────────────────────────

@Serializable
data class EmotionCardDto(
    val id: String,
    @SerialName("emotionType") val emotionType: String,
    val emoji: String,
    val label: String,
    val description: String,
)

@Serializable
data class ScenarioLessonDto(
    val id: String,
    val title: String,
    val situation: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
)

@Serializable
data class JournalEntryDto(
    val id: String,
    val childId: String,
    val emotionType: String,
    val note: String? = null,
    val createdAt: String,
)

@Serializable
data class PracticeAttemptDto(
    val id: String,
    val childId: String,
    val scenarioId: String,
    val selectedIndex: Int,
    val isCorrect: Boolean,
    val promptEmotion: String? = null,
    val createdAt: String,
)

@Serializable
data class ProgressSummaryDto(
    val childId: String,
    val completedLessons: Int,
    val accuracyRate: Float,
    val journalCount: Int,
    val mostMistakenEmotion: String? = null,
)

// ── Request DTOs ──────────────────────────────────────────────────────────────

@Serializable
data class CreateJournalEntryRequest(
    val childId: String,
    val emotionType: String,
    val note: String,
)

@Serializable
data class CreatePracticeAttemptRequest(
    val childId: String,
    val scenarioId: String,
    val selectedIndex: Int,
    val isCorrect: Boolean,
    val promptEmotion: String? = null,
)
