package com.emotionfriend.data.repository

import com.emotionfriend.data.local.JournalEntryDao
import com.emotionfriend.data.mapper.toDomain
import com.emotionfriend.data.mapper.toEntity
import com.emotionfriend.domain.model.JournalEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalJournalRepository @Inject constructor(
    private val dao: JournalEntryDao
) : JournalRepository {

    override fun getAll(): Flow<List<JournalEntry>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getByChildId(childId: String): Flow<List<JournalEntry>> =
        dao.getByChildId(childId).map { list -> list.map { it.toDomain() } }

    override suspend fun insert(entry: JournalEntry) =
        dao.insert(entry.toEntity())

    override suspend fun deleteById(id: String) =
        dao.deleteById(id)
}
