package com.emotionfriend.api.repository

import com.emotionfriend.api.model.MusicTrack

interface MusicRepository {
    suspend fun getAll(): List<MusicTrack>
    suspend fun getById(id: String): MusicTrack?
    suspend fun create(track: MusicTrack): MusicTrack
    suspend fun update(id: String, track: MusicTrack): MusicTrack?
    suspend fun delete(id: String): Boolean
}
