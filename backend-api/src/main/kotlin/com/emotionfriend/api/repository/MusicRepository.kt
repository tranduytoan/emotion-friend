package com.emotionfriend.api.repository

import com.emotionfriend.api.model.MusicTrack

interface MusicRepository {
    suspend fun getAll(): List<MusicTrack>
    suspend fun getById(id: Int): MusicTrack?
    suspend fun create(track: MusicTrack): MusicTrack
    suspend fun update(id: Int, track: MusicTrack): MusicTrack?
    suspend fun delete(id: Int): Boolean
}
