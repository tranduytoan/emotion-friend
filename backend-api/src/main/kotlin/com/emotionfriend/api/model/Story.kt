package com.emotionfriend.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Story(
    val id: Int = 0,
    val title: String,
    val content: String,
    val category: String,
    val imageUrl: String? = null,
    val sortOrder: Int = 0,
)

@Serializable
data class StoryRequest(
    val title: String,
    val content: String,
    val category: String,
    val imageUrl: String? = null,
    val sortOrder: Int = 0,
)
