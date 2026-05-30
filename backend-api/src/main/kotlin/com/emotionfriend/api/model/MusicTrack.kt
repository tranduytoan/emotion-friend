package com.emotionfriend.api.model

import kotlinx.serialization.Serializable

@Serializable
data class MusicTrack(
    val id: Int = 0,
    val title: String,
    val artist: String,
    val filename: String,
    val sortOrder: Int = 0,
)

@Serializable
data class MusicTrackRequest(
    val title: String,
    val artist: String,
    val filename: String,
    val sortOrder: Int = 0,
)
