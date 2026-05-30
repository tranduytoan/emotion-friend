package com.emotionfriend.api.service

import com.emotionfriend.api.model.MusicTrack
import com.emotionfriend.api.repository.MusicRepository

class MusicService(private val repo: MusicRepository) {
    suspend fun getAll(): List<MusicTrack> = repo.getAll()
    suspend fun getById(id: String): MusicTrack = repo.getById(id) ?: throw NoSuchElementException("Track '$id' not found")
    suspend fun create(track: MusicTrack): MusicTrack = repo.create(track)
    suspend fun update(id: String, track: MusicTrack): MusicTrack = repo.update(id, track) ?: throw NoSuchElementException("Track '$id' not found")
    suspend fun delete(id: String): Boolean = repo.delete(id)
}
