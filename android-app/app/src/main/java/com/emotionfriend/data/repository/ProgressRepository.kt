package com.emotionfriend.data.repository

import com.emotionfriend.domain.model.ProgressSummary
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    fun getSummary(childId: String): Flow<ProgressSummary>
}
