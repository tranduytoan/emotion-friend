package com.emotionfriend.api.model

import kotlinx.serialization.Serializable

@Serializable
data class MusicTrack(
    val id: String,
    val title: String,
    val artist: String,
    val filename: String,
    val sortOrder: Int = 0,
)

@Serializable
data class MusicTrackRequest(
    val id: String? = null,
    val title: String,
    val artist: String,
    val filename: String,
    val sortOrder: Int = 0,
)
