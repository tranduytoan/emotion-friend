package com.emotionfriend.domain.model

data class Story(
    val id: String,
    val title: String,
    val content: String,
    val images: List<String>,
    val category: String = "DEFAULT",
    val imageFolder: String? = null,
)
