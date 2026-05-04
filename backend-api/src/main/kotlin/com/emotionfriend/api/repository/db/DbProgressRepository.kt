package com.emotionfriend.api.repository.db

import com.emotionfriend.api.db.JournalEntryTable
import com.emotionfriend.api.db.PracticeAttemptTable
import com.emotionfriend.api.model.EmotionType
import com.emotionfriend.api.model.ProgressSummary
import com.emotionfriend.api.repository.ProgressRepository
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class DbProgressRepository : ProgressRepository {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun getProgressSummary(childId: String): ProgressSummary = dbQuery {
        val attempts = PracticeAttemptTable
            .selectAll()
            .where { PracticeAttemptTable.childId eq childId }
            .toList()

        val journalCount = JournalEntryTable
            .selectAll()
            .where { JournalEntryTable.childId eq childId }
            .count()
            .toInt()

        val completedLessons = attempts.map { it[PracticeAttemptTable.scenarioId] }.distinct().size
        val totalAttempts = attempts.size
        val correctAttempts = attempts.count { it[PracticeAttemptTable.isCorrect] }
        val accuracyRate = if (totalAttempts == 0) 0f else correctAttempts.toFloat() / totalAttempts

        // Find the scenario answered incorrectly the most
        val mostMistakenScenarioId = attempts
            .filter { !it[PracticeAttemptTable.isCorrect] }
            .groupBy { it[PracticeAttemptTable.scenarioId] }
            .maxByOrNull { it.value.size }
            ?.key

        // Resolve scenario to emotion via promptEmotion column when available
        val mostMistakenEmotion: EmotionType? = if (mostMistakenScenarioId != null) {
            attempts
                .filter { !it[PracticeAttemptTable.isCorrect] && it[PracticeAttemptTable.scenarioId] == mostMistakenScenarioId }
                .mapNotNull { it[PracticeAttemptTable.promptEmotion] }
                .firstOrNull()
                ?.let { runCatching { EmotionType.valueOf(it) }.getOrNull() }
        } else null

        ProgressSummary(
            childId             = childId,
            completedLessons    = completedLessons,
            accuracyRate        = accuracyRate,
            journalCount        = journalCount,
            mostMistakenEmotion = mostMistakenEmotion,
        )
    }
}
