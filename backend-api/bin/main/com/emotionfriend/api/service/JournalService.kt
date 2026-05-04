package com.emotionfriend.api.service

import com.emotionfriend.api.model.JournalEntry
import com.emotionfriend.api.repository.JournalRepository

class JournalService(private val repo: JournalRepository) {
    suspend fun create(entry: JournalEntry): JournalEntry = repo.create(entry)
    suspend fun getAllByChildId(childId: String): List<JournalEntry> = repo.getAllByChildId(childId)
}
