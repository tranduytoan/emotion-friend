package com.emotionfriend.data.repository

import com.emotionfriend.data.local.JournalEntryDao
import com.emotionfriend.data.local.PracticeAttemptDao
import com.emotionfriend.domain.model.EmotionType
import com.emotionfriend.domain.model.ProgressSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalProgressRepository @Inject constructor(
    private val practiceDao: PracticeAttemptDao,
    private val journalDao: JournalEntryDao
) : ProgressRepository {

    override fun getSummary(childId: String): Flow<ProgressSummary> {
        val attemptsFlow = practiceDao.getByChildId(childId)
        val journalFlow  = journalDao.getByChildId(childId)

        return combine(attemptsFlow, journalFlow) { attempts, entries ->
            val completed = attempts.count { it.isCorrect == true }

            val total = attempts.count { it.isCorrect != null }
            val accuracy = if (total > 0) completed.toFloat() / total.toFloat() else 0f

            val mostMistaken: EmotionType? = attempts
                .filter { it.isCorrect == false && it.correctEmotion != null }
                .groupingBy { it.correctEmotion!! }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key

            ProgressSummary(
                completedLessons     = completed,
                accuracyRate         = accuracy,
                mostMistakenEmotion  = mostMistaken,
                journalCount         = entries.size
            )
        }
    }
}
