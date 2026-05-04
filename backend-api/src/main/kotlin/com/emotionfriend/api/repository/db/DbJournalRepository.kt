package com.emotionfriend.api.repository.db

import com.emotionfriend.api.db.JournalEntryTable
import com.emotionfriend.api.model.EmotionType
import com.emotionfriend.api.model.JournalEntry
import com.emotionfriend.api.repository.JournalRepository
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.Instant
import java.util.UUID

class DbJournalRepository : JournalRepository {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun getAllByChildId(childId: String): List<JournalEntry> = dbQuery {
        JournalEntryTable
            .selectAll()
            .where { JournalEntryTable.childId eq childId }
            .orderBy(JournalEntryTable.createdAt, SortOrder.DESC)
            .map { it.toJournalEntry() }
    }

    override suspend fun create(entry: JournalEntry): JournalEntry = dbQuery {
        val id = UUID.randomUUID().toString()
        val now = Instant.now()
        JournalEntryTable.insert {
            it[JournalEntryTable.id]          = id
            it[JournalEntryTable.childId]     = entry.childId
            it[JournalEntryTable.emotionType] = entry.emotionType.name
            it[JournalEntryTable.note]        = entry.note.ifBlank { null }
            it[JournalEntryTable.createdAt]   = now
        }
        entry.copy(id = id, createdAt = now.toString())
    }

    override suspend fun countByChildId(childId: String): Int = dbQuery {
        JournalEntryTable
            .selectAll()
            .where { JournalEntryTable.childId eq childId }
            .count()
            .toInt()
    }

    private fun ResultRow.toJournalEntry() = JournalEntry(
        id          = this[JournalEntryTable.id],
        childId     = this[JournalEntryTable.childId],
        emotionType = EmotionType.valueOf(this[JournalEntryTable.emotionType]),
        note        = this[JournalEntryTable.note] ?: "",
        createdAt   = this[JournalEntryTable.createdAt].toString(),
    )
}
