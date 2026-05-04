package com.emotionfriend.api.repository

import com.emotionfriend.api.model.PracticeAttempt

interface PracticeRepository {
    suspend fun getAllByChildId(childId: String): List<PracticeAttempt>
    suspend fun create(attempt: PracticeAttempt): PracticeAttempt
}
