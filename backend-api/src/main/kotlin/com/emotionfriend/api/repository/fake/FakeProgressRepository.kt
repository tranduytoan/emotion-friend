package com.emotionfriend.api.repository.fake

import com.emotionfriend.api.model.ProgressSummary
import com.emotionfriend.api.repository.JournalRepository
import com.emotionfriend.api.repository.PracticeRepository
import com.emotionfriend.api.repository.ProgressRepository

class FakeProgressRepository(
    private val journalRepo: JournalRepository,
    private val practiceRepo: PracticeRepository,
) : ProgressRepository {

    override suspend fun getProgressSummary(childId: String): ProgressSummary {
        val attempts = practiceRepo.getAllByChildId(childId)
        val journalCount = journalRepo.countByChildId(childId)

        val completedLessons = attempts.map { it.scenarioId }.distinct().size
        val accuracyRate = if (attempts.isEmpty()) 0f
        else attempts.count { it.isCorrect }.toFloat() / attempts.size

        val mostMistakenScenarioId = attempts
            .filter { !it.isCorrect }
            .groupBy { it.scenarioId }
            .maxByOrNull { it.value.size }
            ?.key

        // mostMistakenEmotion mapping to DB-backed impl later
        return ProgressSummary(
            childId = childId,
            completedLessons = completedLessons,
            accuracyRate = accuracyRate,
            journalCount = journalCount,
            mostMistakenEmotion = null,
        )
    }
}
