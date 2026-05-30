package com.emotionfriend.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Story(
    val id: String,
    val title: String,
    val content: String,
    val category: String,
    val imageUrl: String? = null,
    val sortOrder: Int = 0,
)

@Serializable
data class StoryRequest(
    val id: String? = null,
    val title: String,
    val content: String,
    val category: String,
    val imageUrl: String? = null,
    val sortOrder: Int = 0,
)
