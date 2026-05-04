package com.emotionfriend.api.model

import kotlinx.serialization.Serializable

@Serializable
data class ScenarioLesson(
    val id: String,
    val title: String,
    val situation: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
)
