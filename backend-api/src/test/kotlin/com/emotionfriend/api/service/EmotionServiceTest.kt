package com.emotionfriend.api.service

import com.emotionfriend.api.model.EmotionCard
import com.emotionfriend.api.repository.EmotionRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EmotionServiceTest {
    private class FakeEmotionRepo : EmotionRepository {
        override suspend fun getAll(): List<EmotionCard> = listOf(
            EmotionCard(id = 1, name = "Vui", type = "HAPPY", description = "Mô tả"),
        )

        override suspend fun getById(id: Int): EmotionCard? = if (id == 1) {
            EmotionCard(id = 1, name = "Vui", type = "HAPPY", description = "Mô tả")
        } else {
            null
        }
    }

    private val service = EmotionService(FakeEmotionRepo())

    @Test
    fun `getAll returns full emotion list`() {
        val list = service.getAll()
        assertEquals(1, list.size)
        assertEquals("Vui", list.first().name)
    }

    @Test
    fun `getById returns item when present`() {
        val emotion = service.getById(1)
        assertEquals(1, emotion.id)
        assertEquals("HAPPY", emotion.type)
    }

    @Test
    fun `getById throws when missing`() {
        assertFailsWith<NoSuchElementException> { service.getById(99) }
    }
}
