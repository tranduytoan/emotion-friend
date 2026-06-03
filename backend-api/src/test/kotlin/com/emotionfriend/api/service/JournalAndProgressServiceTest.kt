package com.emotionfriend.api.service

import com.emotionfriend.api.model.EmotionType
import com.emotionfriend.api.model.JournalEntry
import com.emotionfriend.api.model.ProgressSummary
import com.emotionfriend.api.repository.JournalRepository
import com.emotionfriend.api.repository.ProgressRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class JournalAndProgressServiceTest {
    private class FakeJournalRepo : JournalRepository {
        override suspend fun create(entry: JournalEntry): JournalEntry = entry.copy(id = 123, createdAt = "2026-06-03T00:00:00Z")
        override suspend fun getAllByChildId(childId: String): List<JournalEntry> = listOf(
            JournalEntry(id = 123, childId = childId, emotionType = EmotionType.HAPPY, note = "Ghi chú", createdAt = "2026-06-03T00:00:00Z"),
        )
    }

    private class FakeProgressRepo : ProgressRepository {
        override suspend fun getProgressSummary(childId: String): ProgressSummary = ProgressSummary(
            childId = childId,
            completedLessons = 7,
            accuracyRate = 0.86f,
            journalCount = 4,
            mostMistakenEmotion = EmotionType.SAD,
        )
    }

    @Test
    fun `journal service create returns saved entry`() {
        val service = JournalService(FakeJournalRepo())
        val created = service.create(JournalEntry(childId = "child-x", emotionType = EmotionType.CALM, note = "Note"))
        assertEquals(123, created.id)
        assertEquals("2026-06-03T00:00:00Z", created.createdAt)
    }

    @Test
    fun `journal service returns entries for child id`() {
        val service = JournalService(FakeJournalRepo())
        val entries = service.getAllByChildId("child-x")
        assertEquals(1, entries.size)
        assertEquals(EmotionType.HAPPY, entries.first().emotionType)
    }

    @Test
    fun `progress service returns summary data`() {
        val service = ProgressService(FakeProgressRepo())
        val summary = service.getProgress("child-x")
        assertEquals(7, summary.completedLessons)
        assertEquals(0.86f, summary.accuracyRate)
    }
}
