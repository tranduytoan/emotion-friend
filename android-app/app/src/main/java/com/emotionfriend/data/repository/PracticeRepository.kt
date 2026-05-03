package com.emotionfriend.data.repository

import com.emotionfriend.domain.model.PracticeAttempt
import kotlinx.coroutines.flow.Flow

interface PracticeRepository {
    fun getByChildId(childId: String): Flow<List<PracticeAttempt>>
    fun getRecentByChildId(childId: String, limit: Int = 50): Flow<List<PracticeAttempt>>
    suspend fun insert(attempt: PracticeAttempt)
}
