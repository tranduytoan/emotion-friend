package com.emotionfriend.data.seed

import kotlinx.serialization.Serializable

/**
 * JSON-deserialization DTOs matching the shape of the seed asset files.
 * These are intentionally separate from remote DTOs because the JSON key
 * names and field semantics differ slightly (e.g. "type" vs "emotionType").
 */

@Serializable
data class SeedEmotionCardDto(
    val id: String,
    val label: String,
    val emoji: String,
    val type: String,
    val description: String,
)

@Serializable
data class SeedScenarioLessonDto(
    val id: String,
    val title: String,
    val situationText: String,
    val imageName: String? = null,
    val correctEmotion: String,
    val options: List<String>,
    val explanation: String,
)

@Serializable
data class SeedStoryDto(
    val id: String,
    val title: String,
    val content: String,
    val image1: String = "",
    val image2: String = "",
    val image3: String = "",
    val image4: String = "",
    val category: String = "DEFAULT",
)
