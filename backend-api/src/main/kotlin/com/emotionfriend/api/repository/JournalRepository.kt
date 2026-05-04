package com.emotionfriend.api.repository

import com.emotionfriend.api.model.JournalEntry

interface JournalRepository {
    suspend fun getAllByChildId(childId: String): List<JournalEntry>
    suspend fun create(entry: JournalEntry): JournalEntry
    suspend fun countByChildId(childId: String): Int
}
