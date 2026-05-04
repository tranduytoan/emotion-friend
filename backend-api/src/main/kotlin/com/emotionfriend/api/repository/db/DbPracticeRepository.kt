package com.emotionfriend.api.repository.db

import com.emotionfriend.api.db.PracticeAttemptTable
import com.emotionfriend.api.model.EmotionType
import com.emotionfriend.api.model.PracticeAttempt
import com.emotionfriend.api.repository.PracticeRepository
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.Instant
import java.util.UUID

class DbPracticeRepository : PracticeRepository {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun getAllByChildId(childId: String): List<PracticeAttempt> = dbQuery {
        PracticeAttemptTable
            .selectAll()
            .where { PracticeAttemptTable.childId eq childId }
            .orderBy(PracticeAttemptTable.createdAt, SortOrder.DESC)
            .map { it.toPracticeAttempt() }
    }

    override suspend fun create(attempt: PracticeAttempt): PracticeAttempt = dbQuery {
        val id = UUID.randomUUID().toString()
        val now = Instant.now()
        PracticeAttemptTable.insert {
            it[PracticeAttemptTable.id]            = id
            it[PracticeAttemptTable.childId]       = attempt.childId
            it[PracticeAttemptTable.scenarioId]    = attempt.scenarioId
            it[PracticeAttemptTable.selectedIndex] = attempt.selectedIndex
            it[PracticeAttemptTable.isCorrect]     = attempt.isCorrect
            it[PracticeAttemptTable.promptEmotion] = attempt.promptEmotion
            it[PracticeAttemptTable.createdAt]     = now
        }
        attempt.copy(id = id, createdAt = now.toString())
    }

    private fun ResultRow.toPracticeAttempt() = PracticeAttempt(
        id            = this[PracticeAttemptTable.id],
        childId       = this[PracticeAttemptTable.childId],
        scenarioId    = this[PracticeAttemptTable.scenarioId],
        selectedIndex = this[PracticeAttemptTable.selectedIndex],
        isCorrect     = this[PracticeAttemptTable.isCorrect],
        promptEmotion = this[PracticeAttemptTable.promptEmotion],
        createdAt     = this[PracticeAttemptTable.createdAt].toString(),
    )
}
