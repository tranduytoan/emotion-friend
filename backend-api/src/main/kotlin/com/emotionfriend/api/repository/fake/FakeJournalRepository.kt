package com.emotionfriend.api.repository.fake

import com.emotionfriend.api.model.JournalEntry
import com.emotionfriend.api.repository.JournalRepository
import java.time.Instant
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

class FakeJournalRepository : JournalRepository {

    private val entries = CopyOnWriteArrayList<JournalEntry>()

    override suspend fun getAllByChildId(childId: String): List<JournalEntry> =
        entries.filter { it.childId == childId }

    override suspend fun create(entry: JournalEntry): JournalEntry {
        val saved = entry.copy(
            id = UUID.randomUUID().toString(),
            createdAt = Instant.now().toString(),
        )
        entries.add(saved)
        return saved
    }

    override suspend fun countByChildId(childId: String): Int =
        entries.count { it.childId == childId }
}
