package com.emotionfriend.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null,
)

/** Response for the mock expression-practice AI evaluation. */
@Serializable
data class ExpressionPracticeResult(
    val matched: Boolean,
    /** Mock confidence score 0.0–1.0. */
    val confidence: Float,
    val feedback: String,
    /** Echo back the prompted emotion for traceability. */
    val promptedEmotion: String,
)
