package com.emotionfriend.api.repository.fake

import com.emotionfriend.api.model.MusicTrack
import com.emotionfriend.api.repository.MusicRepository
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

class FakeMusicRepository : MusicRepository {

    private val tracks = CopyOnWriteArrayList(
        mutableListOf(
            MusicTrack("music-001", "Ánh Nắng Bình Yên", "", "soft_music_1", 1),
            MusicTrack("music-002", "Giọt Mưa Thu", "", "soft_music_2", 2),
            MusicTrack("music-003", "Tiếng Sóng Biển", "", "soft_music_3", 3),
            MusicTrack("music-004", "Gió Nhẹ Đồng Quê", "", "soft_music_4", 4),
            MusicTrack("music-005", "Suối Reo Sớm Mai", "", "soft_music_5", 5),
            MusicTrack("music-006", "Bầu Trời Xanh", "", "soft_music_6", 6),
        )
    )

    override suspend fun getAll(): List<MusicTrack> = tracks.toList()

    override suspend fun getById(id: String): MusicTrack? = tracks.find { it.id == id }

    override suspend fun create(track: MusicTrack): MusicTrack {
        val newTrack = track.copy(id = track.id.ifBlank { UUID.randomUUID().toString() })
        tracks.add(newTrack)
        return newTrack
    }

    override suspend fun update(id: String, track: MusicTrack): MusicTrack? {
        val idx = tracks.indexOfFirst { it.id == id }
        if (idx < 0) return null
        val updated = track.copy(id = id)
        tracks[idx] = updated
        return updated
    }

    override suspend fun delete(id: String): Boolean {
        return tracks.removeIf { it.id == id }
    }
}
