package com.emotionfriend.domain.model

data class Story(
    val id: String,
    val title: String,
    val content: String,
    val images: List<String>,   // up to 4 drawable resource names
    val category: String = "DEFAULT",
)
