package com.emotionfriend.api.repository.db

import com.emotionfriend.api.db.PracticeAttemptTable
import com.emotionfriend.api.model.PracticeAttempt
import com.emotionfriend.api.repository.PracticeRepository
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

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
        val generatedId = PracticeAttemptTable.insert {
            it[childId] = attempt.childId
            it[scenarioId] = attempt.scenarioId
            it[isCorrect] = attempt.isCorrect
            it[promptEmotion] = attempt.promptEmotion
        }[PracticeAttemptTable.id]

        PracticeAttemptTable
            .selectAll()
            .where { PracticeAttemptTable.id eq generatedId }
            .single()
            .toPracticeAttempt()
    }

    private fun ResultRow.toPracticeAttempt(): PracticeAttempt = PracticeAttempt(
        id = this[PracticeAttemptTable.id],
        childId = this[PracticeAttemptTable.childId],
        scenarioId = this[PracticeAttemptTable.scenarioId],
        isCorrect = this[PracticeAttemptTable.isCorrect],
        promptEmotion = this[PracticeAttemptTable.promptEmotion],
        createdAt = this[PracticeAttemptTable.createdAt].toString(),
    )
}
