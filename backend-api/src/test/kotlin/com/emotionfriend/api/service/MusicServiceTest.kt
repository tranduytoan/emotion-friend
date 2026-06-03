package com.emotionfriend.api.service

import com.emotionfriend.api.model.MusicTrack
import com.emotionfriend.api.repository.MusicRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MusicServiceTest {
    private class FakeMusicRepo : MusicRepository {
        override suspend fun getAll(): List<MusicTrack> = listOf(MusicTrack(id = 1, title = "Track 1", artist = "Artist", filename = "track.mp3"))
        override suspend fun getById(id: Int): MusicTrack? = if (id == 1) MusicTrack(id = 1, title = "Track 1", artist = "Artist", filename = "track.mp3") else null
        override suspend fun create(track: MusicTrack): MusicTrack = track.copy(id = 2)
        override suspend fun update(id: Int, track: MusicTrack): MusicTrack? = if (id == 1) track.copy(id = 1) else null
        override suspend fun delete(id: Int): Boolean = id == 1
    }

    private val service = MusicService(FakeMusicRepo())

    @Test
    fun `getAll returns music tracks`() {
        assertEquals(1, service.getAll().size)
    }

    @Test
    fun `getById throws when track missing`() {
        assertFailsWith<NoSuchElementException> { service.getById(99) }
    }

    @Test
    fun `create returns track with assigned id`() {
        val created = service.create(MusicTrack(title = "New", artist = "A", filename = "new.mp3"))
        assertEquals(2, created.id)
    }

    @Test
    fun `delete returns true for existing track`() {
        assertEquals(true, service.delete(1))
    }

    @Test
    fun `update throws when track missing`() {
        assertFailsWith<NoSuchElementException> {
            service.update(99, MusicTrack(title = "New", artist = "A", filename = "new.mp3"))
        }
    }
}
