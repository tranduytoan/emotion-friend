package com.emotionfriend.data.repository

import com.emotionfriend.domain.model.JournalEntry
import kotlinx.coroutines.flow.Flow

interface JournalRepository {
    fun getAll(): Flow<List<JournalEntry>>
    fun getByChildId(childId: String): Flow<List<JournalEntry>>
    suspend fun insert(entry: JournalEntry)
    suspend fun deleteById(id: String)
}
